package com.ratelimiter.gateway.ratelimiter;

import com.ratelimiter.common.enums.Algorithm;
import com.ratelimiter.gateway.redis.LuaLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ratelimiter.gateway.redis.RedisExecutor;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenBucketRateLimiterTest {

    @Mock RedisExecutor redisExecutor;
    LuaLoader luaLoader = new LuaLoader();

    TokenBucketRateLimiter limiter;

    @BeforeEach
    void setup() {
        limiter = new TokenBucketRateLimiter(redisExecutor, luaLoader, true);
    }

    @Test
    void allowed_whenBucketHasTokens() {
        when(redisExecutor.execute(any(), anyList(), anyList()))
                .thenReturn(Flux.just(List.of(1L, 7L)));

        StepVerifier.create(limiter.isAllowed("client-3", 10, 60))
                .assertNext(r -> {
                    assertThat(r.allowed()).isTrue();
                    assertThat(r.remaining()).isEqualTo(7L);
                })
                .verifyComplete();
    }

    @Test
    void denied_whenBucketIsEmpty() {
        when(redisExecutor.execute(any(), anyList(), anyList()))
                .thenReturn(Flux.just(List.of(0L, 0L)));

        StepVerifier.create(limiter.isAllowed("client-3", 10, 60))
                .assertNext(r -> {
                    assertThat(r.allowed()).isFalse();
                    assertThat(r.remaining()).isZero();
                    assertThat(r.retryAfterSeconds()).isEqualTo(1L); // next refill tick
                })
                .verifyComplete();
    }

    @Test
    void failOpen_whenRedisThrows() {
        when(redisExecutor.execute(any(), anyList(), anyList()))
                .thenReturn(Flux.error(new RuntimeException("timeout")));

        StepVerifier.create(limiter.isAllowed("client-3", 10, 60))
                .assertNext(r -> {
                    assertThat(r.allowed()).isTrue();
                    assertThat(r.remaining()).isEqualTo(-1L);
                })
                .verifyComplete();
    }

    @Test
    void returnsCorrectAlgorithm() {
        assertThat(limiter.getAlgorithm()).isEqualTo(Algorithm.TOKEN_BUCKET);
    }
}
