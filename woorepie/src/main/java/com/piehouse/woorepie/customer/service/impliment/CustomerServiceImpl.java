package com.piehouse.woorepie.customer.service.impliment;

import com.piehouse.woorepie.customer.dto.SessionCustomer;
import com.piehouse.woorepie.customer.dto.request.CreateCustomerRequest;
import com.piehouse.woorepie.customer.dto.request.LoginCustomerRequest;
import com.piehouse.woorepie.customer.dto.response.GetCustomerResponse;
import com.piehouse.woorepie.customer.entity.Account;
import com.piehouse.woorepie.customer.entity.Customer;
import com.piehouse.woorepie.customer.repository.AccountRepository;
import com.piehouse.woorepie.customer.repository.CustomerRepository;
import com.piehouse.woorepie.customer.service.CustomerService;
import com.piehouse.woorepie.estate.service.impliment.EstateServiceImpl;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import com.piehouse.woorepie.global.kafka.dto.CustomerCreatedEvent;
import com.piehouse.woorepie.global.kafka.dto.TransactionCreatedEvent;
import com.piehouse.woorepie.global.kafka.service.KafkaProducerService;
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

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();
    private static final int ACCOUNT_NUMBER_LENGTH = 15;
    private final EstateServiceImpl estateServiceImpl;
    private final KafkaProducerService kafkaProducerService;

    @Override
    public void customerLogin(LoginCustomerRequest customerRequest, HttpServletRequest request) {

        Customer customer = customerRepository.findByCustomerEmail(customerRequest.getCustomerEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(customerRequest.getCustomerPassword(), customer.getCustomerPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (!customer.getCustomerPhoneNumber().equals(customerRequest.getCustomerPhoneNumber())) {
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
                    .accountNumber(generateUniqueAccountNumber())
                    .customerKyc(UUID.randomUUID().toString())
                    .customerIdentificationUrl(customerRequest.getCustomerIdentificationUrl())
                    .build();
            customerRepository.save(customer);

            CustomerCreatedEvent event = CustomerCreatedEvent.fromCustomer(customer);
            kafkaProducerService.sendCustomerCreated(event);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }

    }

    //계좌 번호 생성
    public String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = generateRandomDigits();
        } while (customerRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    private String generateRandomDigits() {
        StringBuilder sb = new StringBuilder(ACCOUNT_NUMBER_LENGTH);
        for (int i = 0; i < ACCOUNT_NUMBER_LENGTH; i++) {
            if(i==3||i==7||i==11) sb.append("-");
            sb.append(secureRandom.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    public GetCustomerResponse getCustomer(SessionCustomer sessionCustomer) {

        Customer customer = customerRepository.findById(sessionCustomer.getCustomerId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        try{

            List<Account> accounts = accountRepository.findByCustomer(customer);

            //토큰 보유액 계산
            int totalAccountTokenPrice = accounts.stream()
                    .mapToInt(account ->
                            account.getAccountTokenAmount()
                                    * estateServiceImpl
                                    .getRedisEstatePrice(account.getEstate().getEstateId())
                                    .getEstateTokenPrice()
                    )
                    .sum();

            return GetCustomerResponse.builder()
                    .customerName(customer.getCustomerName())
                    .customerEmail(customer.getCustomerEmail())
                    .customerPhoneNumber(customer.getCustomerPhoneNumber())
                    .customerAddress(customer.getCustomerAddress())
                    .accountNumber(customer.getAccountNumber())
                    .totalAccountTokenPrice(totalAccountTokenPrice)
                    .accountBalance(customer.getAccountBalance())
                    .customerJoinDate(customer.getCustomerJoinDate())
                    .build();

        }catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

}
