package com.ratelimiter.admin.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "api_usage", indexes = {
    @Index(name = "idx_api_usage_client_timestamp", columnList = "client_id, timestamp"),
    @Index(name = "idx_api_usage_endpoint", columnList = "endpoint"),
    @Index(name = "idx_api_usage_status_code", columnList = "status_code")
})
public class ApiUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "endpoint", nullable = false, length = 255)
    private String endpoint;

    @Column(name = "method", nullable = false, length = 10)
    private String method;

    @Column(name = "status_code", nullable = false)
    private Integer statusCode;

    @Column(name = "response_time_ms", nullable = false)
    private Long responseTimeMs;

    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp = Instant.now();

    public ApiUsage() {
    }

    public ApiUsage(Long id, Client client, String endpoint, String method, Integer statusCode, Long responseTimeMs, Instant timestamp) {
        this.id = id;
        this.client = client;
        this.endpoint = endpoint;
        this.method = method;
        this.statusCode = statusCode;
        this.responseTimeMs = responseTimeMs;
        this.timestamp = timestamp != null ? timestamp : Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public Long getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
