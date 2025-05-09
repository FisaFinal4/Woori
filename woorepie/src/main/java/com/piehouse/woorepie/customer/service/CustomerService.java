package com.piehouse.woorepie.customer.service;

import com.piehouse.woorepie.customer.dto.SessionCustomer;
import com.piehouse.woorepie.customer.dto.request.CreateCustomerRequest;
import com.piehouse.woorepie.customer.dto.request.LoginCustomerRequest;
import com.piehouse.woorepie.customer.dto.response.GetCustomerResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface CustomerService {

    void customerLogin(LoginCustomerRequest customerRequest, HttpServletRequest request);

    void customerLogout(HttpServletRequest request);

    void createCustomer(CreateCustomerRequest customerRequest);

    GetCustomerResponse getCustomer(SessionCustomer sessionCustomer);

}
