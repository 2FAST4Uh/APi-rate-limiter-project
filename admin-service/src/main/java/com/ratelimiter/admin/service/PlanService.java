package com.ratelimiter.admin.service;

import com.ratelimiter.admin.dto.CreatePlanRequest;
import com.ratelimiter.admin.dto.PlanResponse;

import java.util.List;

public interface PlanService {
    PlanResponse createPlan(CreatePlanRequest request);
    List<PlanResponse> getAllPlans();
    PlanResponse getPlanById(Long id);
    PlanResponse updatePlan(Long id, CreatePlanRequest request);
    void deletePlan(Long id);
}
