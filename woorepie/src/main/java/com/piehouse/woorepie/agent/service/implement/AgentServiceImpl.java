package com.piehouse.woorepie.agent.service.implement;

import com.piehouse.woorepie.agent.dto.request.CreateAgentRequest;
import com.piehouse.woorepie.agent.entity.Agent;
import com.piehouse.woorepie.agent.repository.AgentRepository;
import com.piehouse.woorepie.agent.service.AgentService;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void createAgent(CreateAgentRequest agentRequest, HttpServletRequest request) {
        if (agentRepository.existsByAgentEmail(agentRequest.getAgentEmail()) ||
                agentRepository.existsByAgentPhoneNumber(agentRequest.getAgentPhoneNumber())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        }

        try {
            Agent agent = Agent.builder()
                    .agentName(agentRequest.getAgentName())
                    .agentEmail(agentRequest.getAgentEmail())
                    .agentPassword(passwordEncoder.encode(agentRequest.getAgentPassword()))
                    .agentPhoneNumber(agentRequest.getAgentPhoneNumber())
                    .agentDateOfBirth(agentRequest.getAgentDateOfBirth())
                    .agentIdentificationUrl(agentRequest.getAgentIdentificationUrl())
                    .agentCertUrl(agentRequest.getAgentCertUrl())
                    .businessName(agentRequest.getBusinessName())
                    .businessNumber(agentRequest.getBusinessNumber())
                    .businessPhoneNumber(agentRequest.getBusinessPhoneNumber())
                    .businessAddress(agentRequest.getBusinessAddress())
                    .warrantUrl(agentRequest.getWarrantUrl())
                    .agentKyc(UUID.randomUUID().toString())
                    .build();
            agentRepository.save(agent);
        } catch (CustomException e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

}
