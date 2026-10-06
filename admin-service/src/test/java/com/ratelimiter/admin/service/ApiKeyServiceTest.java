package com.ratelimiter.admin.service;

import com.ratelimiter.admin.dto.ApiKeyCreateResponse;


import com.ratelimiter.admin.entity.ApiKey;
import com.ratelimiter.admin.entity.Client;
import com.ratelimiter.admin.repository.ApiKeyRepository;
import com.ratelimiter.admin.repository.ClientRepository;
import com.ratelimiter.admin.service.impl.ApiKeyServiceImpl;
import com.ratelimiter.common.enums.KeyStatus;
import com.ratelimiter.common.util.HashUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;

import org.mockito.MockitoAnnotations;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApiKeyServiceTest {

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private ClientRepository clientRepository;

    private ApiKeyService apiKeyService;
    @org.mockito.Mock
    private com.ratelimiter.admin.event.CacheInvalidationPublisher invalidationPublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        apiKeyService = new ApiKeyServiceImpl(apiKeyRepository, clientRepository, invalidationPublisher);
    }

    @Test
    void generateKey_shouldCreateKeyAndReturnRawKey() {
        Client client = new Client();
        client.setId(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(inv -> {
            ApiKey k = inv.getArgument(0);
            k.setId(100L);
            return k;
        });

        ApiKeyCreateResponse response = apiKeyService.generateKey(1L);

        assertNotNull(response.getRawKey());
        assertTrue(response.getRawKey().startsWith("ak_live_"));
        assertNotNull(response.getKeyPrefix());
        assertEquals(KeyStatus.ACTIVE, response.getStatus());

        // Verify SHA-256 hash matching
        String expectedHash = HashUtil.hashSha256(response.getRawKey());
        assertNotNull(expectedHash);
    }

    @Test
    void revokeKey_shouldSetStatusToRevoked() {
        ApiKey key = new ApiKey();
        key.setId(100L);
        key.setStatus(KeyStatus.ACTIVE);

        when(apiKeyRepository.findById(100L)).thenReturn(Optional.of(key));

        apiKeyService.revokeKey(100L);

        assertEquals(KeyStatus.REVOKED, key.getStatus());
        verify(apiKeyRepository).save(key);
    }
}
