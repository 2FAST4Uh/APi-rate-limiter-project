package com.ratelimiter.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratelimiter.admin.dto.ClientLimitsResponse;
import com.ratelimiter.admin.dto.UpdateClientLimitsRequest;
import com.ratelimiter.admin.service.RateLimitRuleService;
import com.ratelimiter.common.enums.Algorithm;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClientLimitsController.class, excludeAutoConfiguration = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ClientLimitsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RateLimitRuleService rateLimitRuleService;

    @Test
    void updateClientLimits_shouldReturn200() throws Exception {
        UpdateClientLimitsRequest req = new UpdateClientLimitsRequest(120, 5000, 50000, Algorithm.TOKEN_BUCKET);
        ClientLimitsResponse resp = new ClientLimitsResponse(10L, 120, 5000, 50000, Algorithm.TOKEN_BUCKET);

        when(rateLimitRuleService.updateClientLimits(eq(10L), any())).thenReturn(resp);

        mockMvc.perform(put("/admin/clients/10/limits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(10))
                .andExpect(jsonPath("$.requestsPerMinute").value(120))
                .andExpect(jsonPath("$.algorithm").value("TOKEN_BUCKET"));
    }

    @Test
    void getClientLimits_shouldReturn200() throws Exception {
        ClientLimitsResponse resp = new ClientLimitsResponse(10L, 60, 3600, 10000, Algorithm.SLIDING_WINDOW);
        when(rateLimitRuleService.getClientLimits(10L)).thenReturn(resp);

        mockMvc.perform(get("/admin/clients/10/limits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clientId").value(10))
                .andExpect(jsonPath("$.requestsPerMinute").value(60));
    }
}
