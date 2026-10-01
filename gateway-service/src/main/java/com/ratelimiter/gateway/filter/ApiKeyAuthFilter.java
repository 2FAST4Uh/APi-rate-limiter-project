package com.ratelimiter.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratelimiter.common.dto.ErrorResponse;
import com.ratelimiter.common.util.HashUtil;
import com.ratelimiter.gateway.cache.CachedClientRule;
import com.ratelimiter.gateway.cache.ClientRuleCache;
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

@Component
public class ApiKeyAuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthFilter.class);

    /** Exchange attribute key shared with downstream filters (RateLimitFilter, QuotaFilter). */
    public static final String CLIENT_RULE_ATTR = "clientRule";

    private static final String API_KEY_HEADER   = "X-API-Key";
    private static final String CLIENT_ID_HEADER  = "X-Client-Id";

    private final ClientRuleCache clientRuleCache;
    private final ObjectMapper    objectMapper;

    public ApiKeyAuthFilter(ClientRuleCache clientRuleCache, ObjectMapper objectMapper) {
        this.clientRuleCache = clientRuleCache;
        this.objectMapper    = objectMapper;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!exchange.getRequest().getPath().value().startsWith("/gateway/")) {
            return chain.filter(exchange);
        }

        String apiKey = exchange.getRequest().getHeaders().getFirst(API_KEY_HEADER);
        if (apiKey == null || apiKey.isBlank()) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "Missing X-API-Key header");
        }

        String keyHash = HashUtil.hashSha256(apiKey);

        return clientRuleCache.getByKeyHash(keyHash)
                .flatMap(rule -> {
                    // Store rule so RateLimitFilter / QuotaFilter don't re-query
                    exchange.getAttributes().put(CLIENT_RULE_ATTR, rule);
                    ServerWebExchange mutated = exchange.mutate()
                            .request(r -> r.header(CLIENT_ID_HEADER, String.valueOf(rule.getClientId())))
                            .build();
                    return chain.filter(mutated);
                })
                .switchIfEmpty(Mono.defer(() ->
                        writeError(exchange, HttpStatus.UNAUTHORIZED, "Invalid, revoked, or expired API key")));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String message) {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(exchange.getRequest().getPath().value())
                .build();
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            log.error("Error serializing response", e);
            bytes = "{}".getBytes();
        }
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
