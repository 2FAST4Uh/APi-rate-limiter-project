package com.ratelimiter.gateway.repository;

import com.ratelimiter.gateway.model.ClientRecord;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientR2dbcRepository extends ReactiveCrudRepository<ClientRecord, Long> {
}
