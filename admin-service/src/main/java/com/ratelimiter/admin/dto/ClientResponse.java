package com.ratelimiter.admin.dto;

import com.ratelimiter.common.enums.ClientStatus;
import java.time.Instant;

public class ClientResponse {
    private Long id;
    private String name;
    private String email;
    private ClientStatus status;
    private Long planId;
    private String planName;
    private Instant createdAt;

    public ClientResponse() {
    }

    public ClientResponse(Long id, String name, String email, ClientStatus status, Long planId, String planName, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.planId = planId;
        this.planName = planName;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ClientStatus getStatus() {
        return status;
    }

    public void setStatus(ClientStatus status) {
        this.status = status;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
