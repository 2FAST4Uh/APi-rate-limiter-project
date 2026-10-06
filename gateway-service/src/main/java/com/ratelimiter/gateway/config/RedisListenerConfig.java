package com.ratelimiter.gateway.config;

import com.ratelimiter.common.event.CacheInvalidationEvent;
import com.ratelimiter.gateway.cache.CacheInvalidationSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;

/**
 * Registers the reactive Redis pub/sub listener for cache-invalidation events.
 * Uses {@link ReactiveRedisMessageListenerContainer} to stay fully non-blocking
 * inside the WebFlux/Netty event loop.
 */
@Configuration
public class RedisListenerConfig {

    @Bean
    public ReactiveRedisMessageListenerContainer redisMessageListenerContainer(
            ReactiveRedisConnectionFactory connectionFactory,
            CacheInvalidationSubscriber subscriber) {

        ReactiveRedisMessageListenerContainer container =
                new ReactiveRedisMessageListenerContainer(connectionFactory);

        container.receive(ChannelTopic.of(CacheInvalidationEvent.CHANNEL))
                .map(msg -> msg.getMessage())
                .subscribe(subscriber::onMessage);

        return container;
    }
}
