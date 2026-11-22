package com.ratelimiter.gateway.controller;

import com.ratelimiter.gateway.model.UsageLog;
import com.ratelimiter.gateway.repository.UsageLogR2dbcRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/gateway")
public class UsageLogController {

    private final UsageLogR2dbcRepository usageLogRepository;

    @Autowired
    public UsageLogController(UsageLogR2dbcRepository usageLogRepository) {
        this.usageLogRepository = usageLogRepository;
    }

    @GetMapping(value = "/usage-logs", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<UsageLog> getAllUsageLogs() {
        return usageLogRepository.findAll();
    }
}
