package com.ratelimiter.admin.dto;

import com.ratelimiter.common.enums.Algorithm;

public class ClientLimitsResponse {
    private Long clientId;
    private Integer requestsPerMinute;
    private Integer requestsPerHour;
    private Integer dailyQuota;
    private Algorithm algorithm;

    public ClientLimitsResponse() {
    }

    public ClientLimitsResponse(Long clientId, Integer requestsPerMinute, Integer requestsPerHour, Integer dailyQuota, Algorithm algorithm) {
        this.clientId = clientId;
        this.requestsPerMinute = requestsPerMinute;
        this.requestsPerHour = requestsPerHour;
        this.dailyQuota = dailyQuota;
        this.algorithm = algorithm;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
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
