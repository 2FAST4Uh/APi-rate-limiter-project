package com.ratelimiter.admin.controller;

import com.ratelimiter.admin.dto.ApiKeyCreateResponse;
import com.ratelimiter.admin.dto.ApiKeyResponse;
import com.ratelimiter.admin.service.ApiKeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @PostMapping("/clients/{clientId}/keys")
    public ResponseEntity<ApiKeyCreateResponse> generateKey(@PathVariable Long clientId) {
        return new ResponseEntity<>(apiKeyService.generateKey(clientId), HttpStatus.CREATED);
    }

    @GetMapping("/clients/{clientId}/keys")
    public ResponseEntity<List<ApiKeyResponse>> getClientKeys(@PathVariable Long clientId) {
        return ResponseEntity.ok(apiKeyService.getClientKeys(clientId));
    }

    @PostMapping("/keys/{keyId}/rotate")
    public ResponseEntity<ApiKeyCreateResponse> rotateKey(@PathVariable Long keyId) {
        return ResponseEntity.ok(apiKeyService.rotateKey(keyId));
    }

    @DeleteMapping("/keys/{keyId}")
    public ResponseEntity<Void> revokeKey(@PathVariable Long keyId) {
        apiKeyService.revokeKey(keyId);
        return ResponseEntity.noContent().build();
    }
}
