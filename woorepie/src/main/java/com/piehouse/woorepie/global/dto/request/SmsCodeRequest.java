package com.piehouse.woorepie.global.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SmsCodeRequest {

    @NotBlank
    private String phoneNumber;

}
