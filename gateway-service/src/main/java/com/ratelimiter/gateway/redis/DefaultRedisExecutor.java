package com.ratelimiter.gateway.redis;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Default production implementation — delegates directly to Spring's
 * {@link ReactiveStringRedisTemplate}.
 */
@Component
public class DefaultRedisExecutor implements RedisExecutor {

    private final ReactiveStringRedisTemplate redisTemplate;

    public DefaultRedisExecutor(ReactiveStringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Flux<List<Long>> execute(RedisScript<List<Long>> script, List<String> keys, List<String> args) {
        return redisTemplate.execute(script, keys, args);
    }
}
