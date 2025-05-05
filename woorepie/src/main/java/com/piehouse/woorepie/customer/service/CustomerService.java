package com.piehouse.woorepie.customer.service;

import com.piehouse.woorepie.customer.dto.request.CreateCustomerRequest;
import com.piehouse.woorepie.customer.dto.request.LoginCustomerRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface CustomerService {

    public void customerLogin(LoginCustomerRequest customerRequest, HttpServletRequest request);

    public void CreateCustomer(CreateCustomerRequest customerRequest);

}
