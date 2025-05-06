package com.piehouse.woorepie.customer.service;

import com.piehouse.woorepie.customer.dto.SessionCustomer;
import com.piehouse.woorepie.customer.dto.request.CreateCustomerRequest;
import com.piehouse.woorepie.customer.dto.request.LoginCustomerRequest;
import com.piehouse.woorepie.customer.dto.response.GetCustomerResponse;
import com.piehouse.woorepie.customer.entity.Customer;
import jakarta.servlet.http.HttpServletRequest;

public interface CustomerService {

    public void customerLogin(LoginCustomerRequest customerRequest, HttpServletRequest request);

    public void customerLogout(HttpServletRequest request);

    public void createCustomer(CreateCustomerRequest customerRequest);

    public GetCustomerResponse getCustomer(SessionCustomer sessionCustomer);

}
