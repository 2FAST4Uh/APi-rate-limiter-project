-- KEYS[1]: daily quota key (per-client, per-date)
-- ARGV[1]: daily quota limit
-- ARGV[2]: seconds until midnight UTC (TTL)
-- Returns: {1=allowed/0=denied, remaining}
local quota = tonumber(ARGV[1])
local ttl   = tonumber(ARGV[2])

local current = redis.call('INCR', KEYS[1])
if current == 1 then
    redis.call('EXPIRE', KEYS[1], ttl)
end

if current > quota then
    return {0, 0}
end
return {1, quota - current}
