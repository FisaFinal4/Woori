package com.piehouse.woorepie.global.controller;

import com.piehouse.woorepie.customer.dto.SessionCustomer;
import com.piehouse.woorepie.global.dto.response.S3UrlResponse;
import com.piehouse.woorepie.global.response.ApiResponse;
import com.piehouse.woorepie.global.response.ApiResponseUtil;
import com.piehouse.woorepie.global.service.S3Service;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/s3-presigned-url")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("/customer")
    public ResponseEntity<ApiResponse<S3UrlResponse>> getPresignedUrl(@AuthenticationPrincipal SessionCustomer sessionCustomer, HttpServletRequest request) {
        log.info("AWS S3 customer Presigned Url request");
        S3UrlResponse s3UrlResponse = s3Service.generatePresignedUrl(sessionCustomer);
        return ApiResponseUtil.success(s3UrlResponse, request);
    }

}
