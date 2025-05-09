package com.piehouse.woorepie.global.controller;

import com.piehouse.woorepie.global.dto.request.SmsCodeRequest;
import com.piehouse.woorepie.global.dto.request.SmsVerifyRequest;
import com.piehouse.woorepie.global.response.ApiResponse;
import com.piehouse.woorepie.global.response.ApiResponseUtil;
import com.piehouse.woorepie.global.service.SmsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createSmsAuth (@RequestBody @Valid SmsCodeRequest smsCodeRequest, HttpServletRequest request) {
        log.info("SMS Auth customer request");
        smsService.createSmsAuth(smsCodeRequest);
        return ApiResponseUtil.success(null, request);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifySmsCode(@RequestBody @Valid SmsVerifyRequest smsVerifyRequest, HttpServletRequest request) {
        log.info("SMS Verify customer request");
        Boolean valid = smsService.isSmsCodeValid(smsVerifyRequest);
        return ApiResponseUtil.success(valid, request);
    }

}
