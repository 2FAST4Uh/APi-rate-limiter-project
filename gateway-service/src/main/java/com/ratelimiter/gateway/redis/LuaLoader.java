package com.ratelimiter.gateway.redis;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Pre-loads Lua scripts from the classpath at startup.
 * SHA digests are cached by Spring's {@link RedisScript} so the full script
 * is only sent to Redis once (EVALSHA on subsequent calls).
 */
@Component
public class LuaLoader {

    private final RedisScript<List<Long>> fixedWindowScript;
    private final RedisScript<List<Long>> slidingWindowScript;
    private final RedisScript<List<Long>> tokenBucketScript;
    private final RedisScript<List<Long>> dailyQuotaScript;

    @SuppressWarnings("unchecked")
    public LuaLoader() {
        Class<List<Long>> listLong = (Class<List<Long>>) (Class<?>) List.class;
        this.fixedWindowScript  = RedisScript.of(new ClassPathResource("scripts/fixed_window.lua"),  listLong);
        this.slidingWindowScript = RedisScript.of(new ClassPathResource("scripts/sliding_window.lua"), listLong);
        this.tokenBucketScript  = RedisScript.of(new ClassPathResource("scripts/token_bucket.lua"),   listLong);
        this.dailyQuotaScript   = RedisScript.of(new ClassPathResource("scripts/daily_quota.lua"),    listLong);
    }

    public RedisScript<List<Long>> fixedWindow()  { return fixedWindowScript;  }
    public RedisScript<List<Long>> slidingWindow() { return slidingWindowScript; }
    public RedisScript<List<Long>> tokenBucket()  { return tokenBucketScript;  }
    public RedisScript<List<Long>> dailyQuota()   { return dailyQuotaScript;   }
}
