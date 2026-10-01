package com.ratelimiter.admin.controller;

import com.ratelimiter.admin.dto.ClientLimitsResponse;
import com.ratelimiter.admin.dto.UpdateClientLimitsRequest;
import com.ratelimiter.admin.service.RateLimitRuleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/clients/{clientId}/limits")
public class ClientLimitsController {

    private final RateLimitRuleService rateLimitRuleService;

    public ClientLimitsController(RateLimitRuleService rateLimitRuleService) {
        this.rateLimitRuleService = rateLimitRuleService;
    }

    @PutMapping
    public ResponseEntity<ClientLimitsResponse> updateClientLimits(
            @PathVariable Long clientId,
            @Valid @RequestBody UpdateClientLimitsRequest request) {
        return ResponseEntity.ok(rateLimitRuleService.updateClientLimits(clientId, request));
    }

    @GetMapping
    public ResponseEntity<ClientLimitsResponse> getClientLimits(@PathVariable Long clientId) {
        return ResponseEntity.ok(rateLimitRuleService.getClientLimits(clientId));
    }
}
