package com.ratelimiter.admin.dto;

import com.ratelimiter.common.enums.KeyStatus;
import java.time.Instant;

public class ApiKeyCreateResponse {
    private Long id;
    private Long clientId;
    private String rawKey;
    private String keyPrefix;
    private KeyStatus status;
    private Instant createdAt;
    private Instant expiresAt;

    public ApiKeyCreateResponse() {
    }

    public ApiKeyCreateResponse(Long id, Long clientId, String rawKey, String keyPrefix, KeyStatus status, Instant createdAt, Instant expiresAt) {
        this.id = id;
        this.clientId = clientId;
        this.rawKey = rawKey;
        this.keyPrefix = keyPrefix;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getRawKey() {
        return rawKey;
    }

    public void setRawKey(String rawKey) {
        this.rawKey = rawKey;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public KeyStatus getStatus() {
        return status;
    }

    public void setStatus(KeyStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}
