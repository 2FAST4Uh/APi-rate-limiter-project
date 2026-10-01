package com.ratelimiter.admin.service.impl;

import com.ratelimiter.admin.dto.ApiKeyCreateResponse;
import com.ratelimiter.admin.dto.ApiKeyResponse;
import com.ratelimiter.admin.entity.ApiKey;
import com.ratelimiter.admin.entity.Client;
import com.ratelimiter.admin.repository.ApiKeyRepository;
import com.ratelimiter.admin.repository.ClientRepository;
import com.ratelimiter.admin.service.ApiKeyService;
import com.ratelimiter.common.enums.KeyStatus;
import com.ratelimiter.common.exception.ResourceNotFoundException;
import com.ratelimiter.common.util.KeyGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final ClientRepository clientRepository;

    public ApiKeyServiceImpl(ApiKeyRepository apiKeyRepository, ClientRepository clientRepository) {
        this.apiKeyRepository = apiKeyRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public ApiKeyCreateResponse generateKey(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        KeyGenerator.GeneratedKey generated = KeyGenerator.generateKey();

        ApiKey apiKey = new ApiKey();
        apiKey.setClient(client);
        apiKey.setKeyPrefix(generated.keyPrefix());
        apiKey.setKeyHash(generated.keyHash());
        apiKey.setStatus(KeyStatus.ACTIVE);

        ApiKey saved = apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(
                saved.getId(),
                client.getId(),
                generated.rawKey(),
                saved.getKeyPrefix(),
                saved.getStatus(),
                saved.getCreatedAt(),
                saved.getExpiresAt()
        );
    }

    @Override
    public ApiKeyCreateResponse rotateKey(Long keyId) {
        ApiKey oldKey = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> new ResourceNotFoundException("API key not found with id: " + keyId));

        oldKey.setStatus(KeyStatus.REVOKED);
        apiKeyRepository.save(oldKey);

        return generateKey(oldKey.getClient().getId());
    }

    @Override
    public void revokeKey(Long keyId) {
        ApiKey apiKey = apiKeyRepository.findById(keyId)
                .orElseThrow(() -> new ResourceNotFoundException("API key not found with id: " + keyId));

        apiKey.setStatus(KeyStatus.REVOKED);
        apiKeyRepository.save(apiKey);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiKeyResponse> getClientKeys(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client not found with id: " + clientId);
        }
        return apiKeyRepository.findByClientId(clientId).stream()
                .map(key -> new ApiKeyResponse(
                        key.getId(),
                        key.getClient().getId(),
                        key.getKeyPrefix(),
                        key.getStatus(),
                        key.getCreatedAt(),
                        key.getExpiresAt()
                ))
                .toList();
    }
}
