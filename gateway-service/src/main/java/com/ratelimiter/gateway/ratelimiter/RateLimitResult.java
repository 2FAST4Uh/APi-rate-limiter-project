package com.ratelimiter.gateway.ratelimiter;

/**
 * Immutable result returned by every rate-limiter algorithm.
 *
 * @param allowed          true if the request is within limits
 * @param remaining        requests remaining in the current window (-1 means unknown / fail-open)
 * @param retryAfterSeconds seconds the caller should wait before retrying (0 when allowed)
 */
public record RateLimitResult(boolean allowed, long remaining, long retryAfterSeconds) {

    public static RateLimitResult allow(long remaining) {
        return new RateLimitResult(true, remaining, 0);
    }

    public static RateLimitResult deny(long retryAfterSeconds) {
        return new RateLimitResult(false, 0, retryAfterSeconds);
    }

    /** Used when Redis is unreachable and fail-open is enabled. */
    public static RateLimitResult failOpen() {
        return new RateLimitResult(true, -1, 0);
    }
}
