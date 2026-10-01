package com.ratelimiter.common.exception;

public class RateLimitExceededException extends ApiException {
    public RateLimitExceededException(String message) {
        super(message, 429);
    }
}
