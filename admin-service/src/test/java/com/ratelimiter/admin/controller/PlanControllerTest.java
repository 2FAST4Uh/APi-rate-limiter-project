package com.ratelimiter.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratelimiter.admin.dto.CreatePlanRequest;
import com.ratelimiter.admin.dto.PlanResponse;
import com.ratelimiter.admin.service.PlanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PlanController.class, excludeAutoConfiguration = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlanService planService;

    @Test
    void createPlan_shouldReturn201() throws Exception {
        CreatePlanRequest req = new CreatePlanRequest("GOLD", 100, 10000);
        PlanResponse resp = new PlanResponse(1L, "GOLD", 100, 10000, Instant.now());

        when(planService.createPlan(any())).thenReturn(resp);

        mockMvc.perform(post("/admin/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("GOLD"));
    }

    @Test
    void getAllPlans_shouldReturnList() throws Exception {
        PlanResponse resp = new PlanResponse(1L, "GOLD", 100, 10000, Instant.now());
        when(planService.getAllPlans()).thenReturn(List.of(resp));

        mockMvc.perform(get("/admin/plans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("GOLD"));
    }

    @Test
    void createPlan_validationError_shouldReturn400() throws Exception {
        CreatePlanRequest req = new CreatePlanRequest("", -5, 0);

        mockMvc.perform(post("/admin/plans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }
}
