package com.piehouse.woorepie.agent.service;

import com.piehouse.woorepie.agent.dto.request.CreateAgentRequest;
import com.piehouse.woorepie.agent.dto.request.LoginAgentRequest;
import com.piehouse.woorepie.agent.dto.response.GetAgentResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AgentService {

    void loginAgent(LoginAgentRequest agentRequest, HttpServletRequest request);

    void logoutAgent(HttpServletRequest request);

    void createAgent(CreateAgentRequest agentRequest, HttpServletRequest request);

    GetAgentResponse getAgentInfo(Long agentId);

}
