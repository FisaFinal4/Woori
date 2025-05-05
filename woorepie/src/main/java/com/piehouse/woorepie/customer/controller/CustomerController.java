package com.piehouse.woorepie.customer.controller;

import com.piehouse.woorepie.customer.dto.request.CreateCustomerRequest;
import com.piehouse.woorepie.customer.dto.request.LoginCustomerRequest;
import com.piehouse.woorepie.customer.service.CustomerService;
import com.piehouse.woorepie.global.response.ApiResponse;
import com.piehouse.woorepie.global.response.ApiResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
        customerService.customerLogin(dto, request);
        return ApiResponseUtil.success(null, request);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> signCustomer(@Valid @RequestBody CreateCustomerRequest customerRequest, HttpServletRequest request) {
        log.info("Signing agent request");
        customerService.CreateCustomer(customerRequest);
        return ApiResponseUtil.of(HttpStatus.CREATED,"고객 가입 성공", null, request);
    }

}


