package com.ratelimiter.admin.controller;

import com.ratelimiter.admin.dto.ApiKeyCreateResponse;
import com.ratelimiter.admin.dto.ApiKeyResponse;
import com.ratelimiter.admin.service.ApiKeyService;
import com.ratelimiter.common.enums.KeyStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ApiKeyController.class, excludeAutoConfiguration = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ApiKeyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiKeyService apiKeyService;

    @Test
    void generateKey_shouldReturn201WithRawKey() throws Exception {
        ApiKeyCreateResponse resp = new ApiKeyCreateResponse(1L, 10L, "ak_live_abc123raw", "ak_live_abc1", KeyStatus.ACTIVE, Instant.now(), null);
        when(apiKeyService.generateKey(10L)).thenReturn(resp);

        mockMvc.perform(post("/admin/clients/10/keys"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.rawKey").value("ak_live_abc123raw"))
                .andExpect(jsonPath("$.keyPrefix").value("ak_live_abc1"));
    }

    @Test
    void getClientKeys_shouldReturnListOfKeys() throws Exception {
        ApiKeyResponse resp = new ApiKeyResponse(1L, 10L, "ak_live_abc1", KeyStatus.ACTIVE, Instant.now(), null);
        when(apiKeyService.getClientKeys(10L)).thenReturn(List.of(resp));

        mockMvc.perform(get("/admin/clients/10/keys"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].keyPrefix").value("ak_live_abc1"));
    }

    @Test
    void rotateKey_shouldReturnNewKeyWithRawKey() throws Exception {
        ApiKeyCreateResponse resp = new ApiKeyCreateResponse(2L, 10L, "ak_live_new456raw", "ak_live_new4", KeyStatus.ACTIVE, Instant.now(), null);
        when(apiKeyService.rotateKey(1L)).thenReturn(resp);

        mockMvc.perform(post("/admin/keys/1/rotate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.rawKey").value("ak_live_new456raw"));
    }

    @Test
    void revokeKey_shouldReturn204() throws Exception {
        doNothing().when(apiKeyService).revokeKey(1L);

        mockMvc.perform(delete("/admin/keys/1"))
                .andExpect(status().isNoContent());
    }
}
