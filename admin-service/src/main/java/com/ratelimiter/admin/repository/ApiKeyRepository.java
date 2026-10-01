package com.ratelimiter.admin.repository;

import com.ratelimiter.admin.entity.ApiKey;
import com.ratelimiter.common.enums.KeyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {
    Optional<ApiKey> findByKeyHash(String keyHash);
    List<ApiKey> findByClientId(Long clientId);
    List<ApiKey> findByClientIdAndStatus(Long clientId, KeyStatus status);
}
