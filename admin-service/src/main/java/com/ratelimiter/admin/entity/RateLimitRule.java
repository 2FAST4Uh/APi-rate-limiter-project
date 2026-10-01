package com.ratelimiter.admin.entity;

import com.ratelimiter.common.enums.Algorithm;
import jakarta.persistence.*;

@Entity
@Table(name = "rate_limit_rules")
public class RateLimitRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    @Column(name = "requests_per_minute", nullable = false)
    private Integer requestsPerMinute;

    @Column(name = "requests_per_hour", nullable = false)
    private Integer requestsPerHour;

    @Column(name = "daily_quota", nullable = false)
    private Integer dailyQuota;

    @Enumerated(EnumType.STRING)
    @Column(name = "algorithm", nullable = false, length = 50)
    private Algorithm algorithm = Algorithm.SLIDING_WINDOW;

    public RateLimitRule() {
    }

    public RateLimitRule(Long id, Client client, Plan plan, Integer requestsPerMinute, Integer requestsPerHour, Integer dailyQuota, Algorithm algorithm) {
        this.id = id;
        this.client = client;
        this.plan = plan;
        this.requestsPerMinute = requestsPerMinute;
        this.requestsPerHour = requestsPerHour;
        this.dailyQuota = dailyQuota;
        this.algorithm = algorithm != null ? algorithm : Algorithm.SLIDING_WINDOW;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Integer getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public void setRequestsPerMinute(Integer requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    public Integer getRequestsPerHour() {
        return requestsPerHour;
    }

    public void setRequestsPerHour(Integer requestsPerHour) {
        this.requestsPerHour = requestsPerHour;
    }

    public Integer getDailyQuota() {
        return dailyQuota;
    }

    public void setDailyQuota(Integer dailyQuota) {
        this.dailyQuota = dailyQuota;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }
}
