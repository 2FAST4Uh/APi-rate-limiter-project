package com.ratelimiter.gateway.ratelimiter;

import com.ratelimiter.common.enums.Algorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Looks up the correct {@link RateLimiter} implementation by algorithm name.
 * Adding a new algorithm = implement {@link RateLimiter}, annotate with
 * {@code @Component}, and it is picked up automatically.
 */
@Component
public class RateLimiterFactory {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterFactory.class);

    private final Map<Algorithm, RateLimiter> limiterMap;

    public RateLimiterFactory(List<RateLimiter> rateLimiters) {
        limiterMap = new EnumMap<>(Algorithm.class);
        rateLimiters.forEach(rl -> limiterMap.put(rl.getAlgorithm(), rl));
        log.info("Registered rate-limiter algorithms: {}", limiterMap.keySet());
    }

    public RateLimiter getLimiter(Algorithm algorithm) {
        RateLimiter limiter = limiterMap.get(algorithm);
        if (limiter == null) {
            log.warn("No limiter for algorithm {}, falling back to SLIDING_WINDOW", algorithm);
            return limiterMap.get(Algorithm.SLIDING_WINDOW);
        }
        return limiter;
    }
}
