package com.ratelimiter.admin.event;

import com.ratelimiter.common.event.CacheInvalidationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes cache-invalidation messages to Redis so the gateway can
 * evict stale entries from its in-memory {@code ClientRuleCache} immediately
 * instead of waiting for the TTL to expire.
 *
 * <p>Failures are logged and swallowed — Redis being unavailable must not
 * block the admin write path.
 */
@Component
public class CacheInvalidationPublisher {

    private static final Logger log = LoggerFactory.getLogger(CacheInvalidationPublisher.class);

    private final StringRedisTemplate redisTemplate;

    public CacheInvalidationPublisher(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /** Called when a specific API key is revoked or rotated. */
    public void publishKeyInvalidation(String keyHash) {
        publish(CacheInvalidationEvent.forKeyHash(keyHash));
    }

    /** Called when a client's rate-limit rule is updated. */
    public void publishClientInvalidation(Long clientId) {
        publish(CacheInvalidationEvent.forClient(clientId));
    }

    private void publish(String payload) {
        try {
            redisTemplate.convertAndSend(CacheInvalidationEvent.CHANNEL, payload);
            log.debug("Published cache invalidation: {}", payload);
        } catch (Exception e) {
            log.warn("Failed to publish cache invalidation '{}': {}", payload, e.getMessage());
        }
    }
}
