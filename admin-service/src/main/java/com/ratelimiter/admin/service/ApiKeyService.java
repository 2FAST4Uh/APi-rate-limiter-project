package com.ratelimiter.admin.service;

import com.ratelimiter.admin.dto.ApiKeyCreateResponse;
import com.ratelimiter.admin.dto.ApiKeyResponse;

import java.util.List;

public interface ApiKeyService {
    ApiKeyCreateResponse generateKey(Long clientId);
    ApiKeyCreateResponse rotateKey(Long keyId);
    void revokeKey(Long keyId);
    List<ApiKeyResponse> getClientKeys(Long clientId);
}
