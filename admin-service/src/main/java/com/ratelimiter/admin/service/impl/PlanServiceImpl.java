package com.ratelimiter.admin.service.impl;

import com.ratelimiter.admin.dto.CreatePlanRequest;
import com.ratelimiter.admin.dto.PlanResponse;
import com.ratelimiter.admin.entity.Plan;
import com.ratelimiter.admin.repository.PlanRepository;
import com.ratelimiter.admin.service.PlanService;
import com.ratelimiter.common.exception.ApiException;
import com.ratelimiter.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    public PlanServiceImpl(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public PlanResponse createPlan(CreatePlanRequest request) {
        if (planRepository.existsByName(request.getName())) {
            throw new ApiException("Plan with name '" + request.getName() + "' already exists", 400);
        }
        Plan plan = new Plan();
        plan.setName(request.getName());
        plan.setRequestsPerMinute(request.getRequestsPerMinute());
        plan.setDailyQuota(request.getDailyQuota());

        Plan saved = planRepository.save(plan);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanResponse> getAllPlans() {
        return planRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PlanResponse getPlanById(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + id));
        return mapToResponse(plan);
    }

    @Override
    public PlanResponse updatePlan(Long id, CreatePlanRequest request) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + id));

        if (!plan.getName().equals(request.getName()) && planRepository.existsByName(request.getName())) {
            throw new ApiException("Plan with name '" + request.getName() + "' already exists", 400);
        }

        plan.setName(request.getName());
        plan.setRequestsPerMinute(request.getRequestsPerMinute());
        plan.setDailyQuota(request.getDailyQuota());

        return mapToResponse(planRepository.save(plan));
    }

    @Override
    public void deletePlan(Long id) {
        if (!planRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plan not found with id: " + id);
        }
        planRepository.deleteById(id);
    }

    private PlanResponse mapToResponse(Plan plan) {
        return new PlanResponse(
                plan.getId(),
                plan.getName(),
                plan.getRequestsPerMinute(),
                plan.getDailyQuota(),
                plan.getCreatedAt()
        );
    }
}
