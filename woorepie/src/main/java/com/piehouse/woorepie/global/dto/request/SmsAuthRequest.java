package com.piehouse.woorepie.global.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SmsAuthRequest {

    @NotBlank
    private String phoneNumber;

}
