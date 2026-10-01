package com.ratelimiter.admin.service.impl;

import com.ratelimiter.admin.dto.ClientLimitsResponse;
import com.ratelimiter.admin.dto.UpdateClientLimitsRequest;
import com.ratelimiter.admin.entity.Client;
import com.ratelimiter.admin.entity.RateLimitRule;
import com.ratelimiter.admin.repository.ClientRepository;
import com.ratelimiter.admin.repository.RateLimitRuleRepository;
import com.ratelimiter.admin.service.RateLimitRuleService;
import com.ratelimiter.common.enums.Algorithm;
import com.ratelimiter.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RateLimitRuleServiceImpl implements RateLimitRuleService {

    private final RateLimitRuleRepository rateLimitRuleRepository;
    private final ClientRepository clientRepository;

    public RateLimitRuleServiceImpl(RateLimitRuleRepository rateLimitRuleRepository, ClientRepository clientRepository) {
        this.rateLimitRuleRepository = rateLimitRuleRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientLimitsResponse updateClientLimits(Long clientId, UpdateClientLimitsRequest request) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        RateLimitRule rule = rateLimitRuleRepository.findByClientId(clientId)
                .orElse(new RateLimitRule());

        rule.setClient(client);
        rule.setRequestsPerMinute(request.getRequestsPerMinute());
        rule.setRequestsPerHour(request.getRequestsPerHour());
        rule.setDailyQuota(request.getDailyQuota());
        rule.setAlgorithm(request.getAlgorithm());

        RateLimitRule saved = rateLimitRuleRepository.save(rule);

        return new ClientLimitsResponse(
                clientId,
                saved.getRequestsPerMinute(),
                saved.getRequestsPerHour(),
                saved.getDailyQuota(),
                saved.getAlgorithm()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public ClientLimitsResponse getClientLimits(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + clientId));

        RateLimitRule rule = rateLimitRuleRepository.findByClientId(clientId)
                .orElseGet(() -> {
                    RateLimitRule defaultRule = new RateLimitRule();
                    if (client.getPlan() != null) {
                        defaultRule.setRequestsPerMinute(client.getPlan().getRequestsPerMinute());
                        defaultRule.setRequestsPerHour(client.getPlan().getRequestsPerMinute() * 60);
                        defaultRule.setDailyQuota(client.getPlan().getDailyQuota());
                    } else {
                        defaultRule.setRequestsPerMinute(60);
                        defaultRule.setRequestsPerHour(3600);
                        defaultRule.setDailyQuota(10000);
                    }
                    defaultRule.setAlgorithm(Algorithm.SLIDING_WINDOW);
                    return defaultRule;
                });

        return new ClientLimitsResponse(
                clientId,
                rule.getRequestsPerMinute(),
                rule.getRequestsPerHour(),
                rule.getDailyQuota(),
                rule.getAlgorithm()
        );
    }
}
