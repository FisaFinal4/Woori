package com.piehouse.woorepie.agent.controller;

import com.piehouse.woorepie.agent.dto.request.CreateAgentRequest;
import com.piehouse.woorepie.agent.dto.request.LoginAgentRequest;
import com.piehouse.woorepie.agent.service.AgentService;
import com.piehouse.woorepie.global.response.ApiResponse;
import com.piehouse.woorepie.global.response.ApiResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/agent")
public class AgentController {

    private final AgentService agentService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> loginAgent(@Valid @RequestBody LoginAgentRequest loginAgentRequest, HttpServletRequest request) {
        log.info("Login agent request");
        agentService.loginAgent(loginAgentRequest, request);
        return ApiResponseUtil.success(null, request);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createAgent(@Valid @RequestBody CreateAgentRequest agentRequest, HttpServletRequest request) {
        log.info("Signing agent request");
        agentService.createAgent(agentRequest, request);
        return ApiResponseUtil.of(HttpStatus.CREATED,"대행인 가입 성공", null, request);
    }


}
