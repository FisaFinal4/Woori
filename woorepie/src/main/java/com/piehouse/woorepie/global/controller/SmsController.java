package com.piehouse.woorepie.global.controller;

import com.piehouse.woorepie.global.dto.request.SmsAuthRequest;
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
    public ResponseEntity<ApiResponse<Void>> createSmsAuth (@RequestBody @Valid SmsAuthRequest smsAuthRequest, HttpServletRequest request) {
        log.info("SMS Auth customer request");
        smsService.createSmsAuth(smsAuthRequest);
        return ApiResponseUtil.success(null, request);
    }

}
