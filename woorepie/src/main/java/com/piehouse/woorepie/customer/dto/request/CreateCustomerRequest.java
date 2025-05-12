package com.piehouse.woorepie.customer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class CreateCustomerRequest {

    @NotBlank(message = "customerName은 필수입니다.")
    private String customerName;

    @Email
    @NotBlank(message = "customerEmail은 필수입니다.")
    private String customerEmail;

    @NotBlank(message = "customerPassword은 필수입니다.")
    private String customerPassword;

    @NotBlank(message = "customerPhoneNumber은 필수입니다.")
    private String customerPhoneNumber;

    @NotBlank(message = "customerAddress은 필수입니다.")
    private String customerAddress;

    @Past(message = "customerDateOfBirth은 과거여야 합니다.")
    @NotNull(message = "customerDateOfBirth은 필수입니다.")
    private LocalDate customerDateOfBirth;

    @NotBlank(message = "customerIdentificationUrl은 필수입니다.")
    private String customerIdentificationUrl;

}
