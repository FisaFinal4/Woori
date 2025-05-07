package com.piehouse.woorepie.customer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CreateCustomerRequest {

    @NotBlank
    String customerName;

    @Email @NotBlank
    String customerEmail;

    @NotBlank
    String customerPassword;

    @NotBlank
    String customerPhoneNumber;

    @NotBlank
    String customerAddress;

    @NotNull
    @Past
    LocalDate customerDateOfBirth;

    @NotNull
    String customerIdentificationUrl;

}
