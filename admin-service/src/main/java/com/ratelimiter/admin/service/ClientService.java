package com.ratelimiter.admin.service;

import com.ratelimiter.admin.dto.ClientResponse;
import com.ratelimiter.admin.dto.CreateClientRequest;

import java.util.List;

public interface ClientService {
    ClientResponse createClient(CreateClientRequest request);
    List<ClientResponse> getAllClients();
    ClientResponse getClientById(Long id);
}
