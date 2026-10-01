package com.ratelimiter.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratelimiter.common.dto.ErrorResponse;
import com.ratelimiter.common.enums.Algorithm;
import com.ratelimiter.gateway.cache.CachedClientRule;
import com.ratelimiter.gateway.ratelimiter.RateLimitResult;
import com.ratelimiter.gateway.ratelimiter.RateLimiter;
import com.ratelimiter.gateway.ratelimiter.RateLimiterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;

/**
 * Per-minute rate-limit enforcement. Runs after {@link ApiKeyAuthFilter}
 * which populates the {@code clientRule} exchange attribute.
 */
@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RateLimitFilter.class);

    private final RateLimiterFactory factory;
    private final ObjectMapper objectMapper;

    public RateLimitFilter(RateLimiterFactory factory, ObjectMapper objectMapper) {
        this.factory = factory;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        CachedClientRule rule = exchange.getAttribute(ApiKeyAuthFilter.CLIENT_RULE_ATTR);
        if (rule == null) {
            // Not a gateway route or unauthenticated (ApiKeyAuthFilter already handled 401)
            return chain.filter(exchange);
        }

        String clientId = String.valueOf(rule.getClientId());
        Algorithm algorithm;
        try {
            algorithm = Algorithm.valueOf(rule.getAlgorithm());
        } catch (IllegalArgumentException e) {
            log.warn("Unknown algorithm '{}' for client {}, falling back to SLIDING_WINDOW",
                    rule.getAlgorithm(), clientId);
            algorithm = Algorithm.SLIDING_WINDOW;
        }

        RateLimiter limiter = factory.getLimiter(algorithm);

        return limiter.isAllowed(clientId, rule.getRequestsPerMinute(), 60)
                .flatMap(result -> {
                    if (result.allowed()) {
                        exchange.getResponse().getHeaders()
                                .set("X-RateLimit-Limit", String.valueOf(rule.getRequestsPerMinute()));
                        exchange.getResponse().getHeaders()
                                .set("X-RateLimit-Remaining", String.valueOf(Math.max(0, result.remaining())));
                        return chain.filter(exchange);
                    }
                    return write429(exchange, result, "Rate limit exceeded – per-minute quota");
                });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    private Mono<Void> write429(ServerWebExchange exchange, RateLimitResult result, String message) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .error(HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase())
                .message(message)
                .path(exchange.getRequest().getPath().value())
                .build();
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            bytes = "{}".getBytes();
        }
        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().getHeaders().set("Retry-After", String.valueOf(result.retryAfterSeconds()));
        exchange.getResponse().getHeaders().set("X-RateLimit-Remaining", "0");
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
