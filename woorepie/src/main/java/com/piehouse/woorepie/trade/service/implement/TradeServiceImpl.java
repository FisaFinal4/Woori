package com.piehouse.woorepie.trade.service.implement;

import com.piehouse.woorepie.customer.entity.Account;
import com.piehouse.woorepie.customer.entity.Customer;
import com.piehouse.woorepie.customer.repository.AccountRepository;
import com.piehouse.woorepie.customer.repository.CustomerRepository;
import com.piehouse.woorepie.estate.entity.Estate;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import com.piehouse.woorepie.global.kafka.dto.TransactionCreatedEvent;
import com.piehouse.woorepie.global.kafka.dto.OrderCreatedEvent;
import com.piehouse.woorepie.global.kafka.service.KafkaProducerService;
import com.piehouse.woorepie.trade.dto.request.BuyEstateRequest;
import com.piehouse.woorepie.trade.dto.request.RedisCustomerTradeValue;
import com.piehouse.woorepie.trade.dto.request.RedisEstateTradeValue;
import com.piehouse.woorepie.trade.dto.request.SellEstateRequest;
import com.piehouse.woorepie.trade.entity.Trade;
import com.piehouse.woorepie.trade.repository.RedisTradeRepository;
import com.piehouse.woorepie.trade.repository.TradeRepository;
import com.piehouse.woorepie.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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

    @Override
    @Transactional
    public Trade saveTrade(Estate estate, Customer seller, Customer buyer, int tradeTokenAmount, int tokenPrice) {
        // 1. PostgreSQL에 거래 내역 저장
        Trade trade = Trade.builder()
                .estate(estate)
                .seller(seller)
                .buyer(buyer)
                .tradeTokenAmount(tradeTokenAmount)
                .tokenPrice(tokenPrice)
                .tradeDate(LocalDateTime.now())
                .build();

        Trade savedTrade = tradeRepository.save(trade);

        // 2. Kafka로 거래 체결 이벤트 비동기 전송
        TransactionCreatedEvent event = createEvent(savedTrade);
        kafkaProducerService.sendTransactionCreated(event);

        // 3. 판매자 계좌 업데이트
        Account sellerAccount = accountRepository.findByCustomerAndEstate(seller, estate)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NON_EXIST));

        // 거래 금액 계산
        int tradeAmount = tradeTokenAmount * tokenPrice;

        // 판매자 계좌 업데이트 - 토큰과 금액 모두 감소
        sellerAccount.updateTokenAmount(sellerAccount.getAccountTokenAmount() - tradeTokenAmount)
                .updateTotalAmount(sellerAccount.getTotalAccountAmount() - tradeAmount);

        // 4. 구매자 계좌 업데이트
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
    public List<Trade> getTradesByEstateId(Long estateId) {
        return tradeRepository.findByEstate_EstateId(estateId);
    }

    @Override
    public List<Trade> getTradesBySellerId(Long sellerId) {
        return tradeRepository.findBySeller_CustomerId(sellerId);
    }

    @Override
    public List<Trade> getTradesByBuyerId(Long buyerId) {
        return tradeRepository.findByBuyer_CustomerId(buyerId);
    }

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
        int sellAmt = request.getTradeTokenAmount();

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
        System.out.println("getCumulativeSell 들어옴");
        Set<RedisEstateTradeValue> orders = redisOrderRepository.getEstateSellOrders(estateId);
        log.info("매도 요청 누적합 계산을 위한 주문 리스트 확인", orders);
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
}
