package com.ratelimiter.admin.dto;

import java.time.Instant;

public class PlanResponse {
    private Long id;
    private String name;
    private Integer requestsPerMinute;
    private Integer dailyQuota;
    private Instant createdAt;

    public PlanResponse() {
    }

    public PlanResponse(Long id, String name, Integer requestsPerMinute, Integer dailyQuota, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.requestsPerMinute = requestsPerMinute;
        this.dailyQuota = dailyQuota;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRequestsPerMinute() {
        return requestsPerMinute;
    }

    public void setRequestsPerMinute(Integer requestsPerMinute) {
        this.requestsPerMinute = requestsPerMinute;
    }

    public Integer getDailyQuota() {
        return dailyQuota;
    }

    public void setDailyQuota(Integer dailyQuota) {
        this.dailyQuota = dailyQuota;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
