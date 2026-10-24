package com.ratelimiter.gateway.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

/**
 * Immutable usage log entry persisted asynchronously.
 */
@Table("usage_logs")
public class UsageLog {

    @Id
    private Long id;

    @Column("client_id")
    private Long clientId;

    @Column("api_key_id")
    private Long apiKeyId;

    private String path;
    private String method;
    private Integer status;
    private Instant timestamp;
    @Column("latency_ms")
    private Integer latencyMs;

    public UsageLog(Long clientId, Long apiKeyId, String path, String method, Integer status, Instant timestamp, Integer latencyMs) {
        this.clientId = clientId;
        this.apiKeyId = apiKeyId;
        this.path = path;
        this.method = method;
        this.status = status;
        this.timestamp = timestamp;
        this.latencyMs = latencyMs;
    }

    // getters (no setters – immutable for safety)
    public Long getId() { return id; }
    public Long getClientId() { return clientId; }
    public Long getApiKeyId() { return apiKeyId; }
    public String getPath() { return path; }
    public String getMethod() { return method; }
    public Integer getStatus() { return status; }
    public Instant getTimestamp() { return timestamp; }
    public Integer getLatencyMs() { return latencyMs; }
}
