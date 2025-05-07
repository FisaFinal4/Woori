package com.piehouse.woorepie.customer.controller;

import com.piehouse.woorepie.customer.dto.SessionCustomer;
import com.piehouse.woorepie.customer.dto.request.CreateCustomerRequest;
import com.piehouse.woorepie.customer.dto.request.LoginCustomerRequest;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(@RequestBody LoginCustomerRequest dto,
                                   HttpServletRequest request) {
        log.info("Login customer request");
        customerService.customerLogin(dto, request);
        return ApiResponseUtil.success(null, request);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        log.info("Logout customer request");
        customerService.customerLogout(request);
        return ApiResponseUtil.success(null, request);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Void>> createCustomer(@Valid @RequestBody CreateCustomerRequest customerRequest, HttpServletRequest request) {
        log.info("Signing customer request");
        customerService.createCustomer(customerRequest);
        return ApiResponseUtil.of(HttpStatus.CREATED,"고객 가입 성공", null, request);
    }


    @GetMapping
    public ResponseEntity<ApiResponse<GetCustomerResponse>> getCustomer(@AuthenticationPrincipal SessionCustomer sessionCustomer, HttpServletRequest request) {
        log.info("Get customer request");
        GetCustomerResponse getCustomerResponse = customerService.getCustomer(sessionCustomer);
        return ApiResponseUtil.success(getCustomerResponse, request);
    }

}


