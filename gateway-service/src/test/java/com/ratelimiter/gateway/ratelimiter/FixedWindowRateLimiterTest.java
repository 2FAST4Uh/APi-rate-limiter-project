package com.ratelimiter.gateway.ratelimiter;

import com.ratelimiter.common.enums.Algorithm;
import com.ratelimiter.gateway.redis.LuaLoader;
import com.ratelimiter.gateway.redis.RedisExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FixedWindowRateLimiterTest {

    @Mock RedisExecutor redisExecutor;
    LuaLoader luaLoader = new LuaLoader();

    FixedWindowRateLimiter limiter;

    @BeforeEach
    void setup() {
        limiter = new FixedWindowRateLimiter(redisExecutor, luaLoader, true);
    }

    @Test
    void allowed_whenRedisReturnsOne() {
        when(redisExecutor.execute(any(), anyList(), anyList()))
                .thenReturn(Flux.just(List.of(1L, 9L)));

        StepVerifier.create(limiter.isAllowed("client-1", 10, 60))
                .assertNext(r -> {
                    assertThat(r.allowed()).isTrue();
                    assertThat(r.remaining()).isEqualTo(9L);
                })
                .verifyComplete();
    }

    @Test
    void denied_whenRedisReturnsZero() {
        when(redisExecutor.execute(any(), anyList(), anyList()))
                .thenReturn(Flux.just(List.of(0L, 0L)));

        StepVerifier.create(limiter.isAllowed("client-1", 10, 60))
                .assertNext(r -> {
                    assertThat(r.allowed()).isFalse();
                    assertThat(r.remaining()).isZero();
                    assertThat(r.retryAfterSeconds()).isPositive();
                })
                .verifyComplete();
    }

    @Test
    void failOpen_whenRedisThrows() {
        when(redisExecutor.execute(any(), anyList(), anyList()))
                .thenReturn(Flux.error(new RuntimeException("Redis unavailable")));

        StepVerifier.create(limiter.isAllowed("client-1", 10, 60))
                .assertNext(r -> {
                    assertThat(r.allowed()).isTrue();   // fail-open
                    assertThat(r.remaining()).isEqualTo(-1L);
                })
                .verifyComplete();
    }

    @Test
    void returnsCorrectAlgorithm() {
        assertThat(limiter.getAlgorithm()).isEqualTo(Algorithm.FIXED_WINDOW);
    }
}
