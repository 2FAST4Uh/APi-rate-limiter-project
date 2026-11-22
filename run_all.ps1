
# run_all.ps1
# Starts the API Rate Limiter demo on ports 8080, 8081, and 5173.

$ErrorActionPreference = "Stop"

$ProjectRoot = $PSScriptRoot
$LogDir = Join-Path $ProjectRoot "run-logs"
$PidFile = Join-Path $ProjectRoot "run-pids.json"

$Services = @(
    @{
        Name = "gateway-service"
        Directory = "gateway-service"
        Port = 8080
        Command = "maven"
    },
    @{
        Name = "admin-service"
        Directory = "admin-service"
        Port = 8081
        Command = "maven"
    },
    @{
        Name = "admin-dashboard"
        Directory = "admin-dashboard"
        Port = 5173
        Command = "npm"
    }
)

# -------------------- Helpers --------------------

function Stop-PortListener {
    param([int]$Port)

    $connections = @(
        Get-NetTCPConnection -LocalPort $Port -State Listen `
            -ErrorAction SilentlyContinue
    )

    foreach ($connection in $connections) {
        $processId = $connection.OwningProcess

        if ($processId -and $processId -ne $PID) {
            $process = Get-CimInstance Win32_Process `
                -Filter "ProcessId = $processId" `
                -ErrorAction SilentlyContinue

            if ($process) {
                Write-Host "Stopping PID $processId on port $Port ($($process.Name))"
                Stop-Process -Id $processId -Force -ErrorAction SilentlyContinue
            }
        }
    }
}

function Wait-ForPort {
    param(
        [int]$Port,
        [int]$TimeoutSec = 60
    )

    $watch = [Diagnostics.Stopwatch]::StartNew()

    while ($watch.Elapsed.TotalSeconds -lt $TimeoutSec) {
        $client = [Net.Sockets.TcpClient]::new()

        try {
            $task = $client.ConnectAsync("127.0.0.1", $Port)

            if ($task.Wait(500) -and $client.Connected) {
                return $true
            }
        }
        catch {
            # The service is not accepting connections yet.
        }
        finally {
            $client.Dispose()
        }

        Start-Sleep -Milliseconds 300
    }

    return $false
}

function Start-ServiceWindow {
    param(
        [string]$Name,
        [string]$Directory,
        [string]$Command
    )

    $servicePath = Join-Path $ProjectRoot $Directory

    if (-not (Test-Path $servicePath -PathType Container)) {
        throw "Service directory not found: $servicePath"
    }

    $script = if ($Command -eq "maven") {
        if (Test-Path (Join-Path $servicePath "mvnw.cmd")) {
            "call mvnw.cmd spring-boot:run"
        }
        elseif (Test-Path (Join-Path $ProjectRoot "mvnw.cmd")) {
            "call `"$ProjectRoot\mvnw.cmd`" -f pom.xml spring-boot:run"
        }
        else {
            "call mvn spring-boot:run"
        }
    }
    else {
        if (-not (Test-Path (Join-Path $servicePath "package.json"))) {
            throw "package.json not found in $servicePath"
        }

        "call npm run dev"
    }

    $windowCommand = "cd /d `"$servicePath`" && $script"

    Write-Host "Launching $Name ..."
    $process = Start-Process `
        -FilePath "cmd.exe" `
        -ArgumentList @("/k", $windowCommand) `
        -WorkingDirectory $servicePath `
        -PassThru

    return [PSCustomObject]@{
        Name = $Name
        PID = $process.Id
        Port = ($Services | Where-Object Name -eq $Name).Port
    }
}

# -------------------- Validate project --------------------

Write-Host "`n=== API Rate Limiter Demo ===" -ForegroundColor Cyan

foreach ($service in $Services) {
    $path = Join-Path $ProjectRoot $service.Directory

    if (-not (Test-Path $path -PathType Container)) {
        throw "Missing directory: $path. Run this script from your repository."
    }
}

New-Item -ItemType Directory -Path $LogDir -Force | Out-Null

# -------------------- Clean old listeners --------------------

foreach ($service in $Services) {
    Write-Host "Checking port $($service.Port)..."
    Stop-PortListener -Port $service.Port
}

Start-Sleep -Seconds 1

# -------------------- Launch services --------------------

$started = @()

try {
    foreach ($service in $Services) {
        $started += Start-ServiceWindow `
            -Name $service.Name `
            -Directory $service.Directory `
            -Command $service.Command

        Start-Sleep -Seconds 2
    }

    # -------------------- Wait for ports --------------------

    $failed = @()

    foreach ($service in $Services) {
        Write-Host "Waiting for $($service.Name) on port $($service.Port)..."

        if (Wait-ForPort -Port $service.Port -TimeoutSec 90) {
            Write-Host "$($service.Name) is accepting connections." `
                -ForegroundColor Green
        }
        else {
            Write-Host "$($service.Name) did not open port $($service.Port)." `
                -ForegroundColor Red
            $failed += $service
        }
    }

    if ($failed.Count -gt 0) {
        throw "One or more services failed to start."
    }

    # -------------------- Save launcher PIDs --------------------

    $started | ConvertTo-Json |
        Set-Content -Path $PidFile -Encoding UTF8

    # -------------------- Generate API key --------------------

    $apiKey = $null

    try {
        $response = Invoke-RestMethod `
            -Uri "http://127.0.0.1:8081/admin/api-keys" `
            -Method Post `
            -TimeoutSec 15

        if ($response -is [string]) {
            $response = $response | ConvertFrom-Json
        }

        $apiKey = $response.key

        if (-not $apiKey) {
            Write-Warning "The endpoint responded, but no 'key' field was found."
        }
    }
    catch {
        Write-Warning "Could not create an API key: $($_.Exception.Message)"
        Write-Host "Check the endpoint and response format manually."
    }

    # -------------------- Summary --------------------

    Write-Host "`n=== ALL SERVICES STARTED ===" -ForegroundColor Green

    foreach ($item in $started) {
        Write-Host ("{0,-20} PID: {1,-8} Port: {2}" -f `
            $item.Name, $item.PID, $item.Port)
    }

    Write-Host "`nDashboard: http://localhost:5173"
    Write-Host "Gateway:   http://localhost:8080"
    Write-Host "Admin API: http://localhost:8081"

    if ($apiKey) {
        Write-Host "`nNew API key:" -ForegroundColor Cyan
        Write-Host $apiKey
        Write-Host "`nConfigure the dashboard to use this key if required."
    }

    Write-Host "`nLauncher PIDs saved to: $PidFile"
    Write-Host "Each service is running in its own terminal window."
}
catch {
    Write-Host "`nStartup failed: $($_.Exception.Message)" -ForegroundColor Red

    Write-Host "Stopping service windows launched by this script..."

    foreach ($item in $started) {
        Stop-Process -Id $item.PID -Force -ErrorAction SilentlyContinue
    }

    throw
}
