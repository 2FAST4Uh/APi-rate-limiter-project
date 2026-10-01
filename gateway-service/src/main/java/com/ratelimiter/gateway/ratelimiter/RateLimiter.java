package com.ratelimiter.gateway.ratelimiter;

import com.ratelimiter.common.enums.Algorithm;
import reactor.core.publisher.Mono;

/**
 * Strategy interface for rate-limiting algorithms.
 * One Spring bean per algorithm; {@link RateLimiterFactory} selects the right one.
 */
public interface RateLimiter {

    Algorithm getAlgorithm();

    /**
     * Atomically check-and-increment the counter for the given client.
     *
     * @param clientId     client identifier used as the Redis key segment
     * @param limit        max requests allowed in the window
     * @param windowSeconds window duration in seconds
     * @return a {@link Mono} emitting the result; never empty
     */
    Mono<RateLimitResult> isAllowed(String clientId, int limit, int windowSeconds);
}
