package com.ratelimiter.gateway.repository;

import com.ratelimiter.gateway.model.RateLimitRuleRecord;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface RateLimitRuleR2dbcRepository extends ReactiveCrudRepository<RateLimitRuleRecord, Long> {
    Mono<RateLimitRuleRecord> findByClientId(Long clientId);
}
