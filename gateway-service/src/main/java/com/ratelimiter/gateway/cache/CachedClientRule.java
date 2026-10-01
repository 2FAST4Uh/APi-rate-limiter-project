package com.ratelimiter.gateway.cache;

public class CachedClientRule {

    private final Long clientId;
    private final String clientStatus;
    private final int requestsPerMinute;
    private final int requestsPerHour;
    private final int dailyQuota;
    private final String algorithm;

    public CachedClientRule(Long clientId, String clientStatus, int requestsPerMinute,
                            int requestsPerHour, int dailyQuota, String algorithm) {
        this.clientId = clientId;
        this.clientStatus = clientStatus;
        this.requestsPerMinute = requestsPerMinute;
        this.requestsPerHour = requestsPerHour;
        this.dailyQuota = dailyQuota;
        this.algorithm = algorithm;
    }

    public Long getClientId() { return clientId; }
    public String getClientStatus() { return clientStatus; }
    public int getRequestsPerMinute() { return requestsPerMinute; }
    public int getRequestsPerHour() { return requestsPerHour; }
    public int getDailyQuota() { return dailyQuota; }
    public String getAlgorithm() { return algorithm; }
}
