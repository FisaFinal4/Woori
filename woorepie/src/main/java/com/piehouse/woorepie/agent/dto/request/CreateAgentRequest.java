package com.piehouse.woorepie.agent.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
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
    private String agentIdentificationUrlKey;

    @NotNull
    private String agentCertUrlKey;

    @NotBlank
    private String businessName;

    @NotBlank
    private String businessNumber;

    @NotBlank
    private String businessPhoneNumber;

    @NotBlank
    private String businessAddress;

    @NotBlank
    private String warrantUrlKey;

}
