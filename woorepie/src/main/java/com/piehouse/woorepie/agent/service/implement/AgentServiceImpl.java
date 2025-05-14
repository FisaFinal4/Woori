package com.piehouse.woorepie.agent.service.implement;

import com.piehouse.woorepie.agent.dto.SessionAgent;
import com.piehouse.woorepie.agent.dto.request.CreateAgentRequest;
import com.piehouse.woorepie.agent.dto.request.LoginAgentRequest;
import com.piehouse.woorepie.agent.dto.response.GetAgentResponse;
import com.piehouse.woorepie.agent.entity.Agent;
import com.piehouse.woorepie.agent.repository.AgentRepository;
import com.piehouse.woorepie.agent.service.AgentService;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import com.piehouse.woorepie.global.service.implement.S3ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3ServiceImpl s3Service;

    @Override
    public void loginAgent(LoginAgentRequest agentRequest, HttpServletRequest request) {
        Agent agent = agentRepository.findByAgentEmail(agentRequest.getAgentEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(agentRequest.getAgentPassword(), agent.getAgentPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (!agent.getAgentPhoneNumber().equals(agent.getAgentPhoneNumber())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        try {
            SessionAgent principal = SessionAgent.fromAgent(agent);
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    principal.getAuthorities()
            );

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(auth);

            HttpSession session = request.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    context
            );
        }catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }

    }

    @Override
    public void logoutAgent(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                authentication.getAuthorities().stream()
                        .noneMatch(auth -> auth.getAuthority().equals("ROLE_AGENT"))) {
            throw new AccessDeniedException("로그아웃은 AGENT 권한을 가진 사용자만 가능합니다.");
        }

        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
    }

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
                    .agentIdentificationUrl(s3Service.getPublicS3Url(agentRequest.getAgentIdentificationUrlKey()))
                    .agentCertUrl(s3Service.getPublicS3Url(agentRequest.getAgentCertUrlKey()))
                    .businessName(agentRequest.getBusinessName())
                    .businessNumber(agentRequest.getBusinessNumber())
                    .businessPhoneNumber(agentRequest.getBusinessPhoneNumber())
                    .businessAddress(agentRequest.getBusinessAddress())
                    .warrantUrl(s3Service.getPublicS3Url(agentRequest.getWarrantUrlKey()))
                    .agentKyc(UUID.randomUUID().toString())
                    .build();
            agentRepository.save(agent);
        } catch (CustomException e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }
    // agent 정보 조회
    @Override
    public GetAgentResponse getAgentInfo(Long agentId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return GetAgentResponse.builder()
                .agentName(agent.getAgentName())
                .agentPhoneNumber(agent.getAgentPhoneNumber())
                .agentEmail(agent.getAgentEmail())
                .agentDateOfBirth(agent.getAgentDateOfBirth())
                .agentCertUrl(agent.getAgentCertUrl())
                .businessName(agent.getBusinessName())
                .businessNumber(agent.getBusinessNumber())
                .businessAddress(agent.getBusinessAddress())
                .businessPhoneNumber(agent.getBusinessPhoneNumber())
                .warrantUrl(agent.getWarrantUrl())
                .build();
    }

}
