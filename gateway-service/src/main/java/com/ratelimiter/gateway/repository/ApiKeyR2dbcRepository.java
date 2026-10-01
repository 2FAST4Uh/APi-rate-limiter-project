package com.ratelimiter.gateway.repository;

import com.ratelimiter.gateway.model.ApiKeyRecord;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ApiKeyR2dbcRepository extends ReactiveCrudRepository<ApiKeyRecord, Long> {
    Mono<ApiKeyRecord> findByKeyHash(String keyHash);
}
