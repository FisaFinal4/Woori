package com.piehouse.woorepie.customer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginCustomerRequest {

    @Email 
    @NotBlank(message = "customerEmail은 필수입니다.")
    private String customerEmail;

    @NotBlank(message = "customerPassword은 필수입니다.")
    private String customerPassword;

    @NotBlank(message = "customerPhoneNumber은 필수입니다.")
    private String customerPhoneNumber;

}
