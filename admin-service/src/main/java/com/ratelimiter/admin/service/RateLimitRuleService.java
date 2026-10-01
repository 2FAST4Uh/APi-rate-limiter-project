package com.ratelimiter.admin.service;

import com.ratelimiter.admin.dto.ClientLimitsResponse;
import com.ratelimiter.admin.dto.UpdateClientLimitsRequest;

public interface RateLimitRuleService {
    ClientLimitsResponse updateClientLimits(Long clientId, UpdateClientLimitsRequest request);
    ClientLimitsResponse getClientLimits(Long clientId);
}
