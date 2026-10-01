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
public class SlidingWindowRateLimiter implements RateLimiter {

    private static final Logger log = LoggerFactory.getLogger(SlidingWindowRateLimiter.class);

    private final RedisExecutor redisExecutor;
    private final LuaLoader luaLoader;
    private final boolean failOpen;

    public SlidingWindowRateLimiter(RedisExecutor redisExecutor,
                                     LuaLoader luaLoader,
                                     @Value("${gateway.fail-open:true}") boolean failOpen) {
        this.redisExecutor = redisExecutor;
        this.luaLoader = luaLoader;
        this.failOpen = failOpen;
    }

    @Override
    public Algorithm getAlgorithm() {
        return Algorithm.SLIDING_WINDOW;
    }

    @Override
    public Mono<RateLimitResult> isAllowed(String clientId, int limit, int windowSeconds) {
        String key = KeyBuilder.slidingWindow(clientId);
        long nowMs = System.currentTimeMillis();

        return redisExecutor.execute(
                        luaLoader.slidingWindow(),
                        List.of(key),
                        List.of(String.valueOf(limit),
                                String.valueOf(windowSeconds),
                                String.valueOf(nowMs)))
                .next()
                .map(this::toResult)
                .onErrorResume(ex -> {
                    log.warn("SlidingWindow Redis error – fail-{}: {}", failOpen ? "open" : "closed", ex.getMessage());
                    return Mono.just(failOpen ? RateLimitResult.failOpen() : RateLimitResult.deny(windowSeconds));
                });
    }

    private RateLimitResult toResult(List<Long> r) {
        boolean allowed = r.get(0) == 1L;
        long remaining  = r.get(1);
        return allowed ? RateLimitResult.allow(remaining) : RateLimitResult.deny(1);
    }
}
