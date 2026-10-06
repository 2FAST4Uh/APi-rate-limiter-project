package com.ratelimiter.gateway.cache;

import com.ratelimiter.common.event.CacheInvalidationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Receives cache-invalidation messages from the Redis pub/sub channel
 * and evicts stale entries from {@link ClientRuleCache} immediately.
 *
 * <p>This bean is called by {@link com.ratelimiter.gateway.config.RedisListenerConfig}
 * via a reactive subscription — no blocking I/O occurs.
 *
 * <p>Two event types:
 * <ul>
 *   <li>{@code key:<keyHash>}   – evict the exact entry for that key hash</li>
 *   <li>{@code client:<id>}     – evict all entries belonging to that client</li>
 * </ul>
 */
@Component
public class CacheInvalidationSubscriber {

    private static final Logger log = LoggerFactory.getLogger(CacheInvalidationSubscriber.class);

    private final ClientRuleCache clientRuleCache;

    public CacheInvalidationSubscriber(ClientRuleCache clientRuleCache) {
        this.clientRuleCache = clientRuleCache;
    }

    public void onMessage(String payload) {
        log.debug("Received cache invalidation: {}", payload);
        try {
            String type  = CacheInvalidationEvent.type(payload);
            String value = CacheInvalidationEvent.value(payload);

            switch (type) {
                case "key" -> {
                    clientRuleCache.evict(value);
                    log.info("Evicted cache for key hash prefix: {}…",
                            value.substring(0, Math.min(8, value.length())));
                }
                case "client" -> {
                    clientRuleCache.evictByClientId(Long.parseLong(value));
                    log.info("Evicted all cache entries for client id: {}", value);
                }
                default -> log.warn("Unknown invalidation type '{}', ignoring", type);
            }
        } catch (Exception e) {
            log.error("Error processing cache invalidation '{}': {}", payload, e.getMessage());
        }
    }
}
