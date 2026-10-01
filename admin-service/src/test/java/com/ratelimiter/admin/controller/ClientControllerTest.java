package com.ratelimiter.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratelimiter.admin.dto.ClientResponse;
import com.ratelimiter.admin.dto.CreateClientRequest;
import com.ratelimiter.admin.service.ClientService;
import com.ratelimiter.common.enums.ClientStatus;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClientController.class, excludeAutoConfiguration = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ClientService clientService;

    @Test
    void createClient_shouldReturn201() throws Exception {
        CreateClientRequest req = new CreateClientRequest("Acme Corp", "contact@acme.com", 1L);
        ClientResponse resp = new ClientResponse(1L, "Acme Corp", "contact@acme.com", ClientStatus.ACTIVE, 1L, "GOLD", Instant.now());

        when(clientService.createClient(any())).thenReturn(resp);

        mockMvc.perform(post("/admin/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Acme Corp"))
                .andExpect(jsonPath("$.email").value("contact@acme.com"));
    }

    @Test
    void getAllClients_shouldReturnList() throws Exception {
        ClientResponse resp = new ClientResponse(1L, "Acme Corp", "contact@acme.com", ClientStatus.ACTIVE, 1L, "GOLD", Instant.now());
        when(clientService.getAllClients()).thenReturn(List.of(resp));

        mockMvc.perform(get("/admin/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Acme Corp"));
    }
}
