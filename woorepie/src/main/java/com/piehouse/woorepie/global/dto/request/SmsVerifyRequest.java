package com.piehouse.woorepie.global.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SmsVerifyRequest {

    @NotBlank(message = "전화번호는 필수입니다")
    private String phoneNumber;

    @NotBlank(message = "인증코드는 필수입니다")
    private String code;

}
