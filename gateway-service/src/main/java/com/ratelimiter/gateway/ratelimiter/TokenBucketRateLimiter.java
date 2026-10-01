package com.ratelimiter.gateway.ratelimiter;

import com.ratelimiter.common.enums.Algorithm;
import com.ratelimiter.gateway.redis.KeyBuilder;
import com.ratelimiter.gateway.redis.LuaLoader;
import com.ratelimiter.gateway.redis.RedisExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class TokenBucketRateLimiter implements RateLimiter {

    private static final Logger log = LoggerFactory.getLogger(TokenBucketRateLimiter.class);

    private final RedisExecutor redisExecutor;
    private final LuaLoader luaLoader;
    private final boolean failOpen;

    public TokenBucketRateLimiter(RedisExecutor redisExecutor,
                                   LuaLoader luaLoader,
                                   @Value("${gateway.fail-open:true}") boolean failOpen) {
        this.redisExecutor = redisExecutor;
        this.luaLoader = luaLoader;
        this.failOpen = failOpen;
    }

    @Override
    public Algorithm getAlgorithm() {
        return Algorithm.TOKEN_BUCKET;
    }

    @Override
    public Mono<RateLimitResult> isAllowed(String clientId, int limit, int windowSeconds) {
        String key = KeyBuilder.tokenBucket(clientId);
        long nowMs = System.currentTimeMillis();
        // Refill rate = limit / windowSeconds tokens per second.
        double refillRate = (double) limit / windowSeconds;
        // TTL = 2x window so the bucket state persists between sparse requests
        int ttl = windowSeconds * 2;

        return redisExecutor.execute(
                        luaLoader.tokenBucket(),
                        List.of(key),
                        List.of(String.valueOf(limit),
                                String.valueOf(refillRate),
                                String.valueOf(nowMs),
                                String.valueOf(ttl)))
                .next()
                .map(this::toResult)
                .onErrorResume(ex -> {
                    log.warn("TokenBucket Redis error – fail-{}: {}", failOpen ? "open" : "closed", ex.getMessage());
                    return Mono.just(failOpen ? RateLimitResult.failOpen() : RateLimitResult.deny(1));
                });
    }

    private RateLimitResult toResult(List<Long> r) {
        boolean allowed = r.get(0) == 1L;
        long remaining  = r.get(1);
        // Rough retry-after: 1 second (next refill tick)
        return allowed ? RateLimitResult.allow(remaining) : RateLimitResult.deny(1);
    }
}
