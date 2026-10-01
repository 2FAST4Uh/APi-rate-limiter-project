package com.ratelimiter.admin.repository;

import com.ratelimiter.admin.entity.RateLimitRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RateLimitRuleRepository extends JpaRepository<RateLimitRule, Long> {
    Optional<RateLimitRule> findByClientId(Long clientId);
    Optional<RateLimitRule> findByPlanId(Long planId);
}
