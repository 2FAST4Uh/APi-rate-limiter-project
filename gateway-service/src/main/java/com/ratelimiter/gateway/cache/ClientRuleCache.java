package com.ratelimiter.gateway.cache;

import com.ratelimiter.gateway.model.ClientRecord;
import com.ratelimiter.gateway.model.RateLimitRuleRecord;
import com.ratelimiter.gateway.repository.ApiKeyR2dbcRepository;
import com.ratelimiter.gateway.repository.ClientR2dbcRepository;
import com.ratelimiter.gateway.repository.RateLimitRuleR2dbcRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClientRuleCache {

    private static final Logger log = LoggerFactory.getLogger(ClientRuleCache.class);

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final Duration ttl;
    private final ApiKeyR2dbcRepository apiKeyRepository;
    private final ClientR2dbcRepository clientRepository;
    private final RateLimitRuleR2dbcRepository rateLimitRuleRepository;

    public ClientRuleCache(ApiKeyR2dbcRepository apiKeyRepository,
                           ClientR2dbcRepository clientRepository,
                           RateLimitRuleR2dbcRepository rateLimitRuleRepository,
                           @Value("${gateway.cache.ttl-seconds:300}") long ttlSeconds) {
        this.apiKeyRepository = apiKeyRepository;
        this.clientRepository = clientRepository;
        this.rateLimitRuleRepository = rateLimitRuleRepository;
        this.ttl = Duration.ofSeconds(ttlSeconds);
    }

    public Mono<CachedClientRule> getByKeyHash(String keyHash) {
        CacheEntry entry = cache.get(keyHash);
        if (entry != null && !entry.isExpired()) {
            return Mono.just(entry.rule());
        }
        return loadFromDb(keyHash)
                .doOnNext(rule -> cache.put(keyHash, new CacheEntry(rule, Instant.now().plus(ttl))));
    }

    public void evict(String keyHash) {
        cache.remove(keyHash);
    }

    /**
     * Evict every cache entry belonging to a specific client.
     * Used when a rate-limit rule is updated via the admin API.
     */
    public void evictByClientId(Long clientId) {
        cache.entrySet().removeIf(e -> clientId.equals(e.getValue().rule().getClientId()));
    }

    @Scheduled(fixedDelayString = "${gateway.cache.eviction-interval-ms:60000}")
    public void evictExpiredEntries() {
        int removed = 0;
        for (var it = cache.entrySet().iterator(); it.hasNext(); ) {
            if (it.next().getValue().isExpired()) {
                it.remove();
                removed++;
            }
        }
        if (removed > 0) {
            log.debug("Evicted {} expired cache entries", removed);
        }
    }

    private Mono<CachedClientRule> loadFromDb(String keyHash) {
        return apiKeyRepository.findByKeyHash(keyHash)
                .filter(k -> "ACTIVE".equals(k.getStatus()))
                .filter(k -> k.getExpiresAt() == null || k.getExpiresAt().isAfter(Instant.now()))
                .flatMap(apiKey -> clientRepository.findById(apiKey.getClientId())
                        .filter(c -> "ACTIVE".equals(c.getStatus()))
                        .flatMap(client -> rateLimitRuleRepository.findByClientId(client.getId())
                                .defaultIfEmpty(defaultRule(client))
                                .map(rule -> new CachedClientRule(
                                        client.getId(),
                                        client.getStatus(),
                                        rule.getRequestsPerMinute(),
                                        rule.getRequestsPerHour(),
                                        rule.getDailyQuota(),
                                        rule.getAlgorithm()
                                ))));
    }

    private RateLimitRuleRecord defaultRule(ClientRecord client) {
        RateLimitRuleRecord r = new RateLimitRuleRecord();
        r.setClientId(client.getId());
        r.setRequestsPerMinute(60);
        r.setRequestsPerHour(3600);
        r.setDailyQuota(10000);
        r.setAlgorithm("SLIDING_WINDOW");
        return r;
    }

    private record CacheEntry(CachedClientRule rule, Instant expiresAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
