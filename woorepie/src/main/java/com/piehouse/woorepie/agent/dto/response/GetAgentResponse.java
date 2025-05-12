package com.piehouse.woorepie.agent.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class GetAgentResponse {

    private String agentName;

    private String agentPhoneNumber;

    private String agentEmail;

    private LocalDate agentDateOfBirth;

    private String agentCertUrl;

    private String businessName;

    private String businessNumber;

    private String businessAddress;

    private String businessPhoneNumber;

    private String warrantUrl;

}

