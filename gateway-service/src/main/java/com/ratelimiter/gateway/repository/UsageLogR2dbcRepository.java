package com.ratelimiter.gateway.repository;

import com.ratelimiter.gateway.model.UsageLog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsageLogR2dbcRepository extends ReactiveCrudRepository<UsageLog, Long> {
    // Additional query methods can be defined here if needed
}
