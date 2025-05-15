package com.piehouse.woorepie.trade.service.implement;

import com.piehouse.woorepie.customer.entity.Account;
import com.piehouse.woorepie.customer.entity.Customer;
import com.piehouse.woorepie.customer.repository.AccountRepository;
import com.piehouse.woorepie.customer.repository.CustomerRepository;
import com.piehouse.woorepie.estate.dto.RedisEstatePrice;
import com.piehouse.woorepie.estate.entity.Estate;
import com.piehouse.woorepie.estate.entity.EstatePrice;
import com.piehouse.woorepie.estate.repository.EstatePriceRepository;
import com.piehouse.woorepie.estate.repository.EstateRepository;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import com.piehouse.woorepie.global.kafka.dto.SubscriptionRequestEvent;
import com.piehouse.woorepie.global.kafka.dto.TransactionCreatedEvent;
import com.piehouse.woorepie.global.kafka.dto.OrderCreatedEvent;
import com.piehouse.woorepie.global.kafka.service.KafkaProducerService;
import com.piehouse.woorepie.subscription.entity.Subscription;
import com.piehouse.woorepie.trade.dto.request.*;
import com.piehouse.woorepie.trade.entity.Trade;
import com.piehouse.woorepie.trade.repository.RedisTradeRepository;
import com.piehouse.woorepie.trade.repository.TradeRepository;
import com.piehouse.woorepie.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import com.piehouse.woorepie.subscription.repository.SubscriptionRepository;
import org.springframework.transaction.support.TransactionTemplate;
import com.piehouse.woorepie.estate.service.EstateRedisService;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final TradeRepository tradeRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final RedisTradeRepository redisOrderRepository;
    private final KafkaProducerService kafkaProducerService;
    private final EstatePriceRepository estatePriceRepository;
    private final EstateRepository estateRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final StringRedisTemplate redisTemplate;
    private final PlatformTransactionManager transactionManager;
    private static final String REMAINING_TOKENS_KEY_FORMAT = "subscription:%s:remaining-tokens";
    private final EstateRedisService estateRedisService;

    @Override
    @Transactional
    public Trade saveTrade(Estate estate, Customer seller, Customer buyer, int tradeTokenAmount, int tokenPrice) {

        // PostgreSQL 저장
        Trade trade = Trade.builder()
                .estate(estate)
                .seller(seller)
                .buyer(buyer)
                .tradeTokenAmount(tradeTokenAmount)
                .tokenPrice(tokenPrice)
                .tradeDate(LocalDateTime.now())
                .build();
        Trade savedTrade = tradeRepository.save(trade);

        // Kafka로 거래 체결 이벤트 비동기 전송
        TransactionCreatedEvent event = createEvent(savedTrade);
        kafkaProducerService.sendTransactionCreated(event);

        // 판매자 계좌 업데이트
        Account sellerAccount = accountRepository.findByCustomerAndEstate(seller, estate)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NON_EXIST));

        // 거래 금액 계산
        int tradeAmount = tradeTokenAmount * tokenPrice;

        int newTokenAmount = sellerAccount.getAccountTokenAmount() - tradeTokenAmount;
        int newTotalAmount = sellerAccount.getTotalAccountAmount() - tradeAmount;

        // 판매자 계좌 업데이트 - 토큰과 금액 모두 감소
        sellerAccount.updateTokenAmount(newTokenAmount)
                .updateTotalAmount(newTotalAmount);

        // 구매자 계좌 업데이트
        Account buyerAccount = accountRepository.findByCustomerAndEstate(buyer, estate)
                .orElseGet(() -> {
                    // 새 계좌 생성 후 저장
                    Account newAccount = Account.builder()
                            .customer(buyer)
                            .estate(estate)
                            .accountTokenAmount(0)
                            .totalAccountAmount(0)
                            .build();
                    return accountRepository.save(newAccount);
                });

        // 구매자 계좌 업데이트 - 토큰과 금액 모두 증가
        buyerAccount.updateTokenAmount(buyerAccount.getAccountTokenAmount() + tradeTokenAmount)
                .updateTotalAmount(buyerAccount.getTotalAccountAmount() + tradeAmount);

        return savedTrade;

    }

    private TransactionCreatedEvent createEvent(Trade trade) {

        return TransactionCreatedEvent.builder()
                .estateId(trade.getEstate().getEstateId())
                .tradeId(trade.getTradeId())
                .sellerId(trade.getSeller().getCustomerId())
                .buyerId(trade.getBuyer().getCustomerId())
                .tokenPrice(trade.getTokenPrice())
                .tradeTokenAmount(trade.getTradeTokenAmount())
                .tradeDate(trade.getTradeDate())
                .build();

    }

    @Override
    public void buy(BuyEstateRequest request, Long customerId) {

        int amount = request.getTradeTokenAmount();
        int price = request.getTokenPrice();

        if (!isValidBuyRequest(customerId, amount, price)) {
            throw new CustomException(ErrorCode.INSUFFICIENT_CASH);
        }

        OrderCreatedEvent msg = OrderCreatedEvent.builder()
                .estateId(request.getEstateId())
                .customerId(customerId)
                .tokenPrice(price)
                .tradeTokenAmount(amount)
                .build();
        kafkaProducerService.sendOrderCreated(msg);
        log.info("[매수 Kafka 전송 완료] 고객: {}, 수량: {}, 가격: {}", customerId, amount, price);

    }

    private boolean isValidBuyRequest(Long customerId, int newTokenAmount, int newTokenPrice) {

        int newCost = newTokenAmount * newTokenPrice;
        int cumCost = getCumulativeBuyCost(customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NON_EXIST));
        int balance = customer.getAccountBalance();

        log.info("[매수검증] 고객: {}, 기존: {}, 신규: {}, 합계: {}, 잔액: {}",
                customerId, cumCost, newCost, cumCost + newCost, balance);

        return balance >= cumCost + newCost;

    }

    private int getCumulativeBuyCost(Long customerId) {

        Set<RedisCustomerTradeValue> orders = redisOrderRepository.getCustomerBuyOrders(customerId);
        if (orders == null || orders.isEmpty()) {
            log.info("[누적매수] 데이터없음 - customer:{}", customerId);
            return 0;
        }
        return orders.stream()
                .filter(Objects::nonNull)
                .mapToInt(o -> o.getTradeTokenAmount() * o.getTokenPrice())
                .sum();

    }

    @Override
    public void sell(SellEstateRequest request, Long customerId) {

        Long estateId = request.getEstateId();

        // 입력값이 양수더라도 매도(-)기 때문에 음수로 변환해줌
        int sellAmt = -Math.abs(request.getTradeTokenAmount());

        if (!isValidSellRequest(customerId, estateId, sellAmt)) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }

        OrderCreatedEvent msg = OrderCreatedEvent.builder()
                .estateId(estateId)
                .customerId(customerId)
                .tokenPrice(request.getTokenPrice())
                .tradeTokenAmount(sellAmt)
                .build();
        kafkaProducerService.sendOrderCreated(msg);
        log.info("[매도 Kafka 전송 완료] 고객: {}, 부동산: {}, 수량: {}", customerId, estateId, sellAmt);

    }

    private boolean isValidSellRequest(Long customerId, Long estateId, int newSell) {

        log.info("inValidSellRequest는 들어옴");
        int cumSell = getCumulativeSellAmount(customerId, estateId);
        int owned = accountRepository
                .findByCustomer_CustomerIdAndEstate_EstateId(customerId, estateId)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NON_EXIST))
                .getAccountTokenAmount();

        log.info("[매도검증] customerId={}, estateId={}, 보유량={}, 누적매도량={}, 신규매도량={}, 검증합계={}",
                customerId,
                estateId,
                owned,
                cumSell,
                newSell,
                owned + cumSell + newSell
        );

        return owned + cumSell + newSell >= 0;

    }

    private int getCumulativeSellAmount(Long customerId, Long estateId) {

        log.info("getCumulativeSell 들어옴");
        Set<RedisEstateTradeValue> orders = redisOrderRepository.getEstateSellOrders(estateId);
        log.info("매도 요청 누적합 계산을 위한 주문 리스트 확인: {}", orders);

        if (orders == null || orders.isEmpty()) {
            log.info("[누적매도] 데이터없음 - estate:{}, customer:{}", estateId, customerId);
            return 0;
        }

        return orders.stream()
                .filter(Objects::nonNull)
                .filter(o -> o.getCustomerId() != null)
                .filter(o -> o.getCustomerId().equals(customerId) && o.getTradeTokenAmount() < 0)
                .mapToInt(RedisEstateTradeValue::getTradeTokenAmount)
                .sum();

    }

    @Override
    @Transactional
    public void createSubscription(CreateSubscriptionTradeRequest request, Long customerId) {
        Long estateId = request.getEstateId();
        int requestAmount = request.getSubAmount();
        LocalDateTime now = LocalDateTime.now();

        log.info("[청약 신청 시작] customerId: {}, estateId: {}, 신청수량: {}", customerId, estateId, requestAmount);

        // ✅ 1. PostgreSQL 조회해서 청약 기간 검증
        Estate estate = estateRepository.findById(estateId)
                .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));
        LocalDateTime subStart = estate.getSubStartDate();
        LocalDateTime subEnd = estate.getSubEndDate();
        if (subStart == null || subEnd == null || now.isBefore(subStart) || now.isAfter(subEnd)) {
            throw new CustomException(ErrorCode.SUBSCRIPTION_PERIOD_INVALID);
        }

        // ✅ 2. Redis에서 1토큰당 가격 조회
        RedisEstatePrice redisPrice = estateRedisService.getRedisEstatePrice(estateId);
        int tokenPrice = redisPrice.getEstateTokenPrice();
        int subscriptionCost = requestAmount * tokenPrice;

        // ✅ 3. Redis에서 고객의 기존 매수 요청 금액 조회
        Set<RedisCustomerTradeValue> orders = redisOrderRepository.getCustomerBuyOrders(customerId);
        int cumulativeBuyCost = orders == null ? 0 :
                orders.stream()
                        .filter(Objects::nonNull)
                        .mapToInt(o -> o.getTradeTokenAmount() * o.getTokenPrice())
                        .sum();

        // ✅ 4. PostgreSQL: 고객 계좌 조회
        Account account = accountRepository.findByCustomer_CustomerIdAndEstate_EstateId(customerId, estateId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NON_EXIST));
        int userBalance = account.getTotalAccountAmount();

        log.info("[청약 검증] customerId: {}, 기존매수금액: {}, 청약금액: {}, 총합: {}, 잔액: {}",
                customerId, cumulativeBuyCost, subscriptionCost, cumulativeBuyCost + subscriptionCost, userBalance);

        if (userBalance < cumulativeBuyCost + subscriptionCost) {
            throw new CustomException(ErrorCode.INSUFFICIENT_CASH);
        }

        // ✅ 5. Kafka로 청약 요청 전송
        kafkaProducerService.sendSubscriptionRequest(
                SubscriptionRequestEvent.builder()
                        .customerId(customerId)
                        .estateId(estateId)
                        .amount(requestAmount)
                        .subscribeDate(now)
                        .build()
        );
    }




    // 청약 신청 처리 로직
    @Transactional
    public void processSubscription(Long estateId, Long customerId, int requestedAmount, int tokenPrice) {
        // 1. Redis 트랜잭션 시작
        String redisKey = String.format(REMAINING_TOKENS_KEY_FORMAT, estateId);
        Long newRemaining = redisTemplate.opsForValue().decrement(redisKey, requestedAmount);

        if (newRemaining == null || newRemaining < 0) {
            redisTemplate.opsForValue().increment(redisKey, requestedAmount);
            throw new CustomException(ErrorCode.TOKEN_INSUFFICIENT);
        }

        try {
            // 2. PostgreSQL 트랜잭션 시작 (내부)
            executeInTransaction(() -> {
                Estate estate = estateRepository.findById(estateId)
                        .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));

                Customer customer = customerRepository.findById(customerId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                // 청약 저장
                Subscription subscription = Subscription.builder()
                        .estate(estate) // 실제 엔티티 주입
                        .customer(customer) // 실제 엔티티 주입
                        .subTokenAmount(requestedAmount)
                        .build(); // subDate는 @CreationTimestamp로 자동 처리

                subscriptionRepository.save(subscription);

                // 고객 계좌 업데이트
                int totalPrice = requestedAmount * tokenPrice;
                customer.decreaseBalance(totalPrice); // 보유한 계좌에서 감소
                customerRepository.save(customer);
            });
        } catch (Exception e) {
            // PostgreSQL 트랜잭션 실패 → Redis 롤백
            redisTemplate.opsForValue().increment(redisKey, requestedAmount);
            throw e;
        }
    }

    private void executeInTransaction(Runnable action) {

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute(status -> {
            action.run();
            return null;
        });

    }

}

