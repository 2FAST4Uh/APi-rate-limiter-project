package com.ratelimiter.admin.dto;

import com.ratelimiter.common.enums.Algorithm;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateClientLimitsRequest {

    @NotNull(message = "Requests per minute is required")
    @Min(value = 1, message = "Requests per minute must be at least 1")
    private Integer requestsPerMinute;

    @NotNull(message = "Requests per hour is required")
    @Min(value = 1, message = "Requests per hour must be at least 1")
    private Integer requestsPerHour;

    @NotNull(message = "Daily quota is required")
    @Min(value = 1, message = "Daily quota must be at least 1")
    private Integer dailyQuota;

    @NotNull(message = "Algorithm is required")
    private Algorithm algorithm;

    public UpdateClientLimitsRequest() {
    }

    public UpdateClientLimitsRequest(Integer requestsPerMinute, Integer requestsPerHour, Integer dailyQuota, Algorithm algorithm) {
        this.requestsPerMinute = requestsPerMinute;
        this.requestsPerHour = requestsPerHour;
        this.dailyQuota = dailyQuota;
        this.algorithm = algorithm;
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
