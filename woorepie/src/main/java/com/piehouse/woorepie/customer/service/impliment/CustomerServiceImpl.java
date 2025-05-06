package com.piehouse.woorepie.customer.service.impliment;

import com.piehouse.woorepie.customer.dto.SessionCustomer;
import com.piehouse.woorepie.customer.dto.request.CreateCustomerRequest;
import com.piehouse.woorepie.customer.dto.request.LoginCustomerRequest;
import com.piehouse.woorepie.customer.dto.response.GetCustomerResponse;
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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

            SessionCustomer principal = SessionCustomer.fromCustomer(customer);
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    principal,
                    null,
                    principal.getAuthorities()
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
    public void customerLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        SecurityContextHolder.clearContext();
    }

    @Override
    @Transactional
    public void createCustomer(CreateCustomerRequest customerRequest) {
        if (customerRepository.existsByCustomerEmail(customerRequest.getCustomerEmail()) ||
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
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }

    }

    @Override
    public GetCustomerResponse getCustomer(SessionCustomer sessionCustomer) {

        Customer customer = customerRepository.findById(sessionCustomer.getCustomerId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        try{
            GetCustomerResponse getCustomerResponse = GetCustomerResponse.builder()
                    .customerName(customer.getCustomerName())
                    .customerEmail(customer.getCustomerEmail())
                    .customerPhoneNumber(customer.getCustomerPhoneNumber())
                    .customerAddress(customer.getCustomerAddress())
                    .customerJoinDate(customer.getCustomerJoinDate())
                    .build();
            return getCustomerResponse;
        }catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

}
