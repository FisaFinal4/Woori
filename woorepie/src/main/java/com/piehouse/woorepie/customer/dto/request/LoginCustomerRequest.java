package com.piehouse.woorepie.customer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginCustomerRequest {

    @Email @NotBlank
    String customerEmail;

    @NotBlank
    String customerPassword;

}
