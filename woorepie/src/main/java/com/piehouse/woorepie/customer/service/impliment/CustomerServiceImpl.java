package com.piehouse.woorepie.customer.service.impliment;

import com.piehouse.woorepie.customer.dto.request.CreateCustomerRequest;
import com.piehouse.woorepie.customer.entity.Customer;
import com.piehouse.woorepie.customer.repository.CustomerRepository;
import com.piehouse.woorepie.customer.service.CustomerService;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void CreateCustomer(CreateCustomerRequest customerRequest) {
        if (customerRepository.existsByCustomerEmail(customerRequest.getCustomerEmail())||
                customerRepository.existsByCustomerPhoneNumber(customerRequest.getCustomerPhoneNumber())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        }

        try {
            Customer customer = Customer.builder()
                    .customerName(customerRequest.getCustomerName())
                    .customerEmail(customerRequest.getCustomerEmail())
                    .customerPassword(passwordEncoder.encode(customerRequest.getCustomerPassword()))
                    .customerPhoneNumber(customerRequest.getCustomerPhoneNumber())
                    .customerAddress(customerRequest.getCustomerAddress())
                    .customerDateOfBirth(customerRequest.getCustomerDateOfBirth())
                    .build();

            customerRepository.save(customer);
        }catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }

    }

}
