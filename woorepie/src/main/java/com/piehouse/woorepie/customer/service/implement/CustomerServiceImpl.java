package com.piehouse.woorepie.customer.service.implement;

import com.piehouse.woorepie.customer.dto.SessionCustomer;
import com.piehouse.woorepie.customer.dto.request.CreateCustomerRequest;
import com.piehouse.woorepie.customer.dto.request.LoginCustomerRequest;
import com.piehouse.woorepie.customer.dto.response.GetCustomerAccountResponse;
import com.piehouse.woorepie.customer.dto.response.GetCustomerResponse;
import com.piehouse.woorepie.customer.entity.Account;
import com.piehouse.woorepie.customer.entity.Customer;
import com.piehouse.woorepie.customer.repository.AccountRepository;
import com.piehouse.woorepie.customer.repository.CustomerRepository;
import com.piehouse.woorepie.customer.service.CustomerService;
import com.piehouse.woorepie.estate.service.implement.EstateServiceImpl;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import com.piehouse.woorepie.global.kafka.dto.CustomerCreatedEvent;
import com.piehouse.woorepie.global.kafka.service.impliment.KafkaProducerServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();
    private final EstateServiceImpl estateServiceImpl;
    private final KafkaProducerServiceImpl kafkaProducerServiceImpl;
    private static final int ACCOUNT_NUMBER_LENGTH = 15;

    // 로그인
    @Override
    public void customerLogin(LoginCustomerRequest requestDto, HttpServletRequest request) {

        Customer customer = customerRepository.findByCustomerEmail(requestDto.getCustomerEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(requestDto.getCustomerPassword(), customer.getCustomerPassword()) ||
                !customer.getCustomerPhoneNumber().equals(requestDto.getCustomerPhoneNumber())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

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

    }

    // 로그아웃
    @Override
    public void customerLogout(HttpServletRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                authentication.getAuthorities().stream()
                        .noneMatch(auth -> auth.getAuthority().equals("ROLE_CUSTOMER"))) {
            throw new AccessDeniedException("로그아웃은 CUSTOMER 권한을 가진 사용자만 가능합니다.");
        }

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

    }

    // 회원가입
    @Override
    @Transactional
    public void createCustomer(CreateCustomerRequest requestDto) {

        if (customerRepository.existsByCustomerEmail(requestDto.getCustomerEmail()) ||
                customerRepository.existsByCustomerPhoneNumber(requestDto.getCustomerPhoneNumber())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESOURCE);
        }

        Customer customer = Customer.builder()
                .customerName(requestDto.getCustomerName())
                .customerEmail(requestDto.getCustomerEmail())
                .customerPassword(passwordEncoder.encode(requestDto.getCustomerPassword()))
                .customerPhoneNumber(requestDto.getCustomerPhoneNumber())
                .customerAddress(requestDto.getCustomerAddress())
                .customerDateOfBirth(requestDto.getCustomerDateOfBirth())
                .accountNumber(generateUniqueAccountNumber())
                .customerKyc(UUID.randomUUID().toString())
                .customerIdentificationUrl(requestDto.getCustomerIdentificationUrl())
                .build();
        customerRepository.save(customer);

        CustomerCreatedEvent event = CustomerCreatedEvent.fromCustomer(customer);
        kafkaProducerServiceImpl.sendCustomerCreated(event);

    }

    // 계좌 번호 생성
    private String generateUniqueAccountNumber() {

        String accountNumber;
        int maxAttempts = 10;
        int attempt = 0;

        do {
            if (attempt++ > maxAttempts) {
                throw new IllegalStateException("계좌번호 생성 실패: 중복 발생");
            }
            accountNumber = generateRandomDigits();
        } while (customerRepository.existsByAccountNumber(accountNumber));

        return accountNumber;

    }

    private String generateRandomDigits() {

        StringBuilder sb = new StringBuilder(ACCOUNT_NUMBER_LENGTH);

        for (int i = 0; i < ACCOUNT_NUMBER_LENGTH; i++) {
            if (i == 3 || i == 7 || i == 11) sb.append("-");
            sb.append(secureRandom.nextInt(10));
        }

        return sb.toString();

    }

    // 마이페이지 조회
    @Override
    public GetCustomerResponse getCustomer(SessionCustomer session) {

        Customer customer = customerRepository.findById(session.getCustomerId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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
    }

    @Override
    public List<GetCustomerAccountResponse> getCustomerAccount(SessionCustomer session) {

        Customer customer = customerRepository.findById(session.getCustomerId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Account> accounts = accountRepository.findByCustomer(customer);

        List<GetCustomerAccountResponse> accountResponses = accounts.stream()
                .map(account -> GetCustomerAccountResponse.builder()
                        .estateId(account.getEstate().getEstateId())
                        .estateName(account.getEstate().getEstateName())
                        .accountTokenAmount(account.getAccountTokenAmount())
                        .accountTokenPrice(estateServiceImpl.getRedisEstatePrice(account.getEstate().getEstateId()).getEstateTokenPrice())
                        .build())
                .collect(Collectors.toList());

        return accountResponses;
    }

}
