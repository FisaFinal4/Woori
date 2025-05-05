package com.piehouse.woorepie.customer.service.impliment;

import com.piehouse.woorepie.customer.dto.request.CreateCustomerRequest;
import com.piehouse.woorepie.customer.dto.request.LoginCustomerRequest;
import com.piehouse.woorepie.customer.entity.Customer;
import com.piehouse.woorepie.customer.repository.CustomerRepository;
import com.piehouse.woorepie.customer.service.CustomerService;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void customerLogin(LoginCustomerRequest customerRequest, HttpServletRequest request) {

        Customer customer = customerRepository.findByCustomerEmail(customerRequest.getCustomerEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(customerRequest.getCustomerPassword(), customer.getCustomerPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        try {
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    customer.getCustomerId(),
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"))
            );

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(auth);

            HttpSession session = request.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    context
            );
        }catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

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
