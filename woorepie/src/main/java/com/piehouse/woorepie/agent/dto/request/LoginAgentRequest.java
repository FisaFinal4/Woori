package com.piehouse.woorepie.agent.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginAgentRequest {

    @Email
    @NotBlank
    private String agentEmail;

    @NotBlank
    private String agentPassword;

    @NotBlank
    private String agentPhoneNumber;

}
