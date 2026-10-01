package com.ratelimiter.gateway.redis;

import org.springframework.data.redis.core.script.RedisScript;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Thin abstraction over Redis Lua script execution.
 * Exists so unit tests can substitute a simple stub without needing to
 * mock the concrete {@code ReactiveStringRedisTemplate} class (which
 * Mockito cannot inline-mock on JDK 17+ with strong encapsulation).
 */
public interface RedisExecutor {
    Flux<List<Long>> execute(RedisScript<List<Long>> script, List<String> keys, List<String> args);
}
