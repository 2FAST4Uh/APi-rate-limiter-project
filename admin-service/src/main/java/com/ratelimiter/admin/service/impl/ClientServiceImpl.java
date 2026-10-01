package com.ratelimiter.admin.service.impl;

import com.ratelimiter.admin.dto.ClientResponse;
import com.ratelimiter.admin.dto.CreateClientRequest;
import com.ratelimiter.admin.entity.Client;
import com.ratelimiter.admin.entity.Plan;
import com.ratelimiter.admin.repository.ClientRepository;
import com.ratelimiter.admin.repository.PlanRepository;
import com.ratelimiter.admin.service.ClientService;
import com.ratelimiter.common.exception.ApiException;
import com.ratelimiter.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final PlanRepository planRepository;

    public ClientServiceImpl(ClientRepository clientRepository, PlanRepository planRepository) {
        this.clientRepository = clientRepository;
        this.planRepository = planRepository;
    }

    @Override
    public ClientResponse createClient(CreateClientRequest request) {
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("Client with email '" + request.getEmail() + "' already exists", 400);
        }

        Plan plan = null;
        if (request.getPlanId() != null) {
            plan = planRepository.findById(request.getPlanId())
                    .orElseThrow(() -> new ResourceNotFoundException("Plan not found with id: " + request.getPlanId()));
        }

        Client client = new Client();
        client.setName(request.getName());
        client.setEmail(request.getEmail());
        client.setPlan(plan);

        Client saved = clientRepository.save(client);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> getAllClients() {
        return clientRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
        return mapToResponse(client);
    }

    private ClientResponse mapToResponse(Client client) {
        Long planId = client.getPlan() != null ? client.getPlan().getId() : null;
        String planName = client.getPlan() != null ? client.getPlan().getName() : null;

        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getStatus(),
                planId,
                planName,
                client.getCreatedAt()
        );
    }
}
