package com.piehouse.woorepie.agent.service;

import com.piehouse.woorepie.agent.dto.request.CreateAgentRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface AgentService {

    void createAgent(CreateAgentRequest agentRequest, HttpServletRequest request);

}
