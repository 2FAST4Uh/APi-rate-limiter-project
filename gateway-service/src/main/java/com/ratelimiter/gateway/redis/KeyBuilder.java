package com.ratelimiter.gateway.redis;

/**
 * Centralised Redis key factory. All keys share the "rl:" namespace.
 * Format: rl:{algorithm}:{clientId}[:{suffix}]
 */
public final class KeyBuilder {

    private static final String PREFIX = "rl";

    private KeyBuilder() {}

    /** Fixed-window key – includes the window epoch so old windows expire naturally. */
    public static String fixedWindow(String clientId, long windowEpoch) {
        return PREFIX + ":fw:" + clientId + ":" + windowEpoch;
    }

    /** Sliding-window sorted-set key. */
    public static String slidingWindow(String clientId) {
        return PREFIX + ":sw:" + clientId;
    }

    /** Token-bucket hash key. */
    public static String tokenBucket(String clientId) {
        return PREFIX + ":tb:" + clientId;
    }

    /** Daily-quota counter key. */
    public static String dailyQuota(String clientId, String utcDate) {
        return PREFIX + ":quota:" + clientId + ":" + utcDate;
    }
}
