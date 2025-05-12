package com.piehouse.woorepie.customer.controller;

import com.piehouse.woorepie.customer.dto.SessionCustomer;
import com.piehouse.woorepie.customer.dto.request.CreateCustomerRequest;
import com.piehouse.woorepie.customer.dto.response.GetCustomerSubscriptionResponse;
import com.piehouse.woorepie.customer.dto.request.LoginCustomerRequest;
import com.piehouse.woorepie.customer.dto.response.GetCustomerAccountResponse;
import com.piehouse.woorepie.customer.dto.response.GetCustomerResponse;
import com.piehouse.woorepie.customer.service.CustomerService;
import com.piehouse.woorepie.global.response.ApiResponse;
import com.piehouse.woorepie.global.response.ApiResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(@Valid @RequestBody LoginCustomerRequest requestDto, HttpServletRequest request) {
        log.info("Login customer request");
        customerService.customerLogin(requestDto, request);
        return ApiResponseUtil.success(null, request);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        log.info("Logout customer request");
        customerService.customerLogout(request);
        return ApiResponseUtil.success(null, request);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createCustomer(@Valid @RequestBody CreateCustomerRequest requestDto, HttpServletRequest request) {
        log.info("Signing customer request");
        customerService.createCustomer(requestDto);
        return ApiResponseUtil.of(HttpStatus.CREATED,"고객 가입 성공", null, request);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<GetCustomerResponse>> getCustomer(@AuthenticationPrincipal SessionCustomer session, HttpServletRequest request) {
        log.info("Get customer request");
        GetCustomerResponse getCustomerResponse = customerService.getCustomer(session);
        return ApiResponseUtil.success(getCustomerResponse, request);
    }

    @GetMapping("/account")
    public ResponseEntity<ApiResponse<List<GetCustomerAccountResponse>>> getCustomerAccount(@AuthenticationPrincipal SessionCustomer session, HttpServletRequest request) {
        log.info("Get customer account request");
        List<GetCustomerAccountResponse> getCustomerAccountResponseList = customerService.getCustomerAccount(session);
        return ApiResponseUtil.success(getCustomerAccountResponseList, request);
    }

    @GetMapping("/subscription")
    public ResponseEntity<ApiResponse<List<GetCustomerSubscriptionResponse>>> getCustomerSubscription(@AuthenticationPrincipal SessionCustomer session, HttpServletRequest request) {
        log.info("Get customer subscription request");
        List<GetCustomerSubscriptionResponse> getCustomerSubscriptionResponseList = customerService.getCustomerSubscription(session);
        return ApiResponseUtil.success(getCustomerSubscriptionResponseList, request);
    }

}


