package com.piehouse.woorepie.global.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class S3CustomerRequest {

    @NotBlank
    @Email
    private String customerEmail;

}
