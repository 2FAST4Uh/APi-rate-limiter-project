package com.ratelimiter.common.event;

/**
 * Payload published on the Redis channel {@code rl:cache-invalidate}.
 *
 * <p>Format: {@code <type>:<value>}
 * <ul>
 *   <li>{@code key:<keyHash>}   – a specific API key was revoked/rotated</li>
 *   <li>{@code client:<id>}     – a client's rule was updated (evict all its key hashes)</li>
 * </ul>
 */
public final class CacheInvalidationEvent {

    public static final String CHANNEL = "rl:cache-invalidate";

    public static String forKeyHash(String keyHash) {
        return "key:" + keyHash;
    }

    public static String forClient(Long clientId) {
        return "client:" + clientId;
    }

    /** Parse the type prefix. Returns "key" or "client". */
    public static String type(String payload) {
        return payload.substring(0, payload.indexOf(':'));
    }

    /** Parse the value after the type prefix. */
    public static String value(String payload) {
        return payload.substring(payload.indexOf(':') + 1);
    }

    private CacheInvalidationEvent() {}
}
