package com.ratelimiter.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreatePlanRequest {

    @NotBlank(message = "Plan name is required")
    private String name;

    @NotNull(message = "Requests per minute is required")
    @Min(value = 1, message = "Requests per minute must be at least 1")
    private Integer requestsPerMinute;

    @NotNull(message = "Daily quota is required")
    @Min(value = 1, message = "Daily quota must be at least 1")
    private Integer dailyQuota;

    public CreatePlanRequest() {
    }

    public CreatePlanRequest(String name, Integer requestsPerMinute, Integer dailyQuota) {
        this.name = name;
        this.requestsPerMinute = requestsPerMinute;
        this.dailyQuota = dailyQuota;
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
}
