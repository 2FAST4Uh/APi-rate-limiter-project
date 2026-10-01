package com.ratelimiter.common.util;

import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerator {

    private static final String PREFIX = "ak_live_";
    private static final SecureRandom RANDOM = new SecureRandom();

    private KeyGenerator() {
    }

    public static GeneratedKey generateKey() {
        byte[] randomBytes = new byte[24];
        RANDOM.nextBytes(randomBytes);
        String randomStr = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        String rawKey = PREFIX + randomStr;
        String keyPrefix = rawKey.substring(0, 12);
        String keyHash = HashUtil.hashSha256(rawKey);

        return new GeneratedKey(rawKey, keyPrefix, keyHash);
    }

    public record GeneratedKey(String rawKey, String keyPrefix, String keyHash) {}
}
