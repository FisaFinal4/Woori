package com.piehouse.woorepie.global.kafka.service.impliment;

import com.piehouse.woorepie.customer.entity.Account;
import com.piehouse.woorepie.customer.entity.Customer;
import com.piehouse.woorepie.customer.repository.AccountRepository;
import com.piehouse.woorepie.customer.repository.CustomerRepository;
import com.piehouse.woorepie.estate.entity.Dividend;
import com.piehouse.woorepie.estate.entity.Estate;
import com.piehouse.woorepie.estate.entity.EstatePrice;
import com.piehouse.woorepie.estate.repository.DividendRepository;
import com.piehouse.woorepie.estate.repository.EstatePriceRepository;
import com.piehouse.woorepie.estate.repository.EstateRepository;
import com.piehouse.woorepie.estate.service.implement.EstateRedisServiceImpl;
import com.piehouse.woorepie.estate.service.implement.EstateServiceImpl;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import com.piehouse.woorepie.global.kafka.dto.*;
import com.piehouse.woorepie.global.kafka.service.KafkaConsumerService;
import com.piehouse.woorepie.trade.service.TradeRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    private final TradeRedisService tradeRedisService;
    private final EstateRedisServiceImpl estateRedisServiceImpl;
    private final EstateRepository estateRepository;
    private final AccountRepository accountRepository;
    private final DividendRepository dividendRepository;
    private final EstatePriceRepository estatePriceRepository;
    private final CustomerRepository customerRepository;


    @Override
    @KafkaListener(topics = "test", groupId = "group-test")
    public void listenToTopicTest(String message) {
        System.out.println("Received from topic-test: " + message);
    }

    @Override
    @KafkaListener(topics = "order.created", groupId = "group-order")
    public void consumeOrderCreated(OrderCreatedEvent event) {
        log.info("주문 생성 이벤트 수신: {}", event);
        if (event.getTradeTokenAmount() > 0) {
            // 매수 주문
            tradeRedisService.matchNewBuyOrder(event);
        } else if (event.getTradeTokenAmount() < 0) {
            // 매도 주문
            tradeRedisService.matchNewSellOrder(event);
        }
    }

    @Override
    @KafkaListener(topics = "subscription.request", groupId = "subscription-group")
    public void consumeSubscriptionRequest(SubscriptionRequestEvent event) {
        log.info("청약 요청 수신: customerId={}, estateId={}, amount={}, subscribeDate={}",
                event.getCustomerId(), event.getEstateId(), event.getAmount(), event.getSubscribeDate());
        // 토큰 체크 및 결과 처리)은 이후에 구현
    }

    @Override
    @KafkaListener(topics = "subscription.accept", groupId = "estate-consumer")
    @Transactional
    public void handleSubscriptionApproval(SubscriptionAcceptMessage message) {
        log.info("[Kafka] 청약 승인 수신");

        Estate estate = estateRepository.findById(message.getEstateId())
                .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));

        message.getSubCustomer().forEach(subCustomer -> {

            Customer customer = customerRepository.findById(subCustomer.getCustomerId())
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

            Integer tokenPrice = subCustomer.getTokenPrice();
            Integer tokenAmount = subCustomer.getTradeTokenAmount();

            // 계좌 조회: 존재 시 update, 없으면 새로 생성
            Optional<Account> optionalAccount = accountRepository.findByCustomerAndEstate(customer, estate);

            if (optionalAccount.isPresent()) {
                // 계좌 존재 시 업데이트
                Account account = optionalAccount.get();

                int newTokenAmount = account.getAccountTokenAmount() + tokenAmount;
                account.updateTokenAmount(newTokenAmount);

                int newTotalAmount = account.getTotalAccountAmount() + (tokenAmount * tokenPrice);
                account.updateTotalAmount(newTotalAmount);

                accountRepository.save(account);

            } else {
                // 계좌가 없으면 신규 생성
                Account newAccount = Account.builder()
                        .customer(customer)
                        .estate(estate)
                        .accountTokenAmount(tokenAmount)
                        .totalAccountAmount(tokenAmount * tokenPrice)
                        .build();
                accountRepository.save(newAccount);
            }
        });

        // 매물 상태 변경 → SUCCESS
        estate.updateSubStateToSuccess();
        estateRepository.save(estate);

    }

    // 배당금 승인 로직
    @Override
    @KafkaListener(topics = "dividen.accept", groupId = "estate-consumer")
    @Transactional
    public void handleDividendApproval(DividendAcceptMessage message) {

        log.info("[Kafka] 배당금 승인 수신");

        Long estateId = message.getEstateId();
        Integer dividend = message.getDividend();

        // 1. 매물 존재 확인
        Estate estate = estateRepository.findById(estateId)
                .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));

        BigDecimal dividendYield = new BigDecimal(dividend)
                .divide(new BigDecimal(estateRedisServiceImpl.getRedisEstatePrice(estateId).getEstateTokenPrice()), 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));

        // 2. 배당률 저장
        Dividend record = Dividend.builder()
                .estate(estate)
                .dividend(dividend)
                .dividendYield(dividendYield)
                .build();

        dividendRepository.save(record);

        // 3. 해당 estate를 보유한 계좌 전체 조회
        List<Account> accounts = accountRepository.findByEstate(estate);

        for (Account account : accounts) {
            int tokenAmount = account.getAccountTokenAmount();
            Customer customer = account.getCustomer();

            // 배당금 = 보유 수량 * 배당률
            int dividendAmount = dividendYield.multiply(BigDecimal.valueOf(tokenAmount))
                    .setScale(0, RoundingMode.DOWN) // 소수점 절삭
                    .intValue();

            int updatedBalance = customer.getAccountBalance() + dividendAmount;
            customer.setAccountBalance(updatedBalance);
        }

        log.info("배당금 지급 완료 - estateId: {}, 대상 계좌 수: {}", estateId, accounts.size());
    }

    // 매물 매각 승인 로직
    @Override
    @KafkaListener(topics = "exit.accept", groupId = "estate-consumer")
    @Transactional
    public void handleExitApproval(ExitAcceptMessage message) {
        Long estateId = message.getEstateId();

        // 1. 매물 조회
        Estate estate = estateRepository.findById(estateId)
                .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));

        // 2. 상태 EXIT 변경
        estate.updateSubStateToExit();
        estateRepository.save(estate);

        // 3. 최근 시세 조회
        EstatePrice latestPrice = estatePriceRepository
                .findTopByEstate_EstateIdOrderByEstatePriceDateDesc(estateId)
                .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));

        int estatePrice = latestPrice.getEstatePrice();

        // 4. 계좌 조회
        List<Account> accounts = accountRepository.findByEstate(estate);

        for (Account account : accounts) {
            int tokenAmount = account.getAccountTokenAmount();
            Customer customer = account.getCustomer();

            int refundAmount = tokenAmount * estatePrice;

            // 환불 처리
            customer.setAccountBalance(customer.getAccountBalance() + refundAmount);

            // 토큰 소멸 처리
            account.updateTokenAmount(0);
        }

        log.info("매각 환불 및 상태 처리 완료");
    }

}
