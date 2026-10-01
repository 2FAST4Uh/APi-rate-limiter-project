package com.ratelimiter.gateway.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("rate_limit_rules")
public class RateLimitRuleRecord {

    @Id
    @Column("rule_id")
    private Long id;

    @Column("client_id")
    private Long clientId;

    @Column("plan_id")
    private Long planId;

    @Column("requests_per_minute")
    private Integer requestsPerMinute;

    @Column("requests_per_hour")
    private Integer requestsPerHour;

    @Column("daily_quota")
    private Integer dailyQuota;

    @Column("algorithm")
    private String algorithm;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }
    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }
    public Integer getRequestsPerMinute() { return requestsPerMinute; }
    public void setRequestsPerMinute(Integer requestsPerMinute) { this.requestsPerMinute = requestsPerMinute; }
    public Integer getRequestsPerHour() { return requestsPerHour; }
    public void setRequestsPerHour(Integer requestsPerHour) { this.requestsPerHour = requestsPerHour; }
    public Integer getDailyQuota() { return dailyQuota; }
    public void setDailyQuota(Integer dailyQuota) { this.dailyQuota = dailyQuota; }
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
}
