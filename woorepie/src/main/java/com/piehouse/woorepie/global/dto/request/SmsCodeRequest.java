package com.piehouse.woorepie.global.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SmsCodeRequest {

    @NotBlank
    private String phoneNumber;

}
