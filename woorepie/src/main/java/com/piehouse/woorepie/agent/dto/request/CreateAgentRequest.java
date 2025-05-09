package com.piehouse.woorepie.agent.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class CreateAgentRequest {

    @NotBlank
    private String agentName;

    @Email
    @NotBlank
    private String agentEmail;

    @NotBlank
    private String agentPassword;

    @NotBlank
    private String agentPhoneNumber;

    @NotNull
    @Past
    private LocalDate agentDateOfBirth;

    @NotBlank
    private String agentIdentificationUrl;

    @NotNull
    private String agentCertUrl;

    @NotBlank
    private String businessName;

    @NotBlank
    private String businessNumber;

    @NotBlank
    private String businessPhoneNumber;

    @NotBlank
    private String businessAddress;

    @NotBlank
    private String warrantUrl;

}
