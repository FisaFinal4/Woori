package com.piehouse.woorepie.trade.service.implement;

import com.piehouse.woorepie.customer.entity.Customer;
import com.piehouse.woorepie.customer.repository.AccountRepository;
import com.piehouse.woorepie.customer.repository.CustomerRepository;
import com.piehouse.woorepie.estate.entity.entity.Estate;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
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

    @Override
    @Transactional
    public Trade saveTrade(Estate estate, Customer seller, Customer buyer, int tradeTokenAmount, int tokenPrice) {
        Trade trade = Trade.builder()
                .estate(estate)
                .seller(seller)
                .buyer(buyer)
                .tradeTokenAmount(tradeTokenAmount)
                .tokenPrice(tokenPrice)
                .tradeDate(LocalDateTime.now())
                .build();

        return tradeRepository.save(trade);
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
        int price = request.getTradePrice();

        if (!isValidBuyRequest(customerId, amount, price)) {
            throw new CustomException(ErrorCode.INSUFFICIENT_CASH);
        }

        // Kafka 연동 또는 Redis 저장은 추후 구현
        System.out.printf("[매수 처리 완료] 고객: %d, 매수량: %d, 단가: %d\n", customerId, amount, price);
    }

    private boolean isValidBuyRequest(Long customerId, int newTokenAmount, int newTokenPrice) {
        int newBuyCost = newTokenAmount * newTokenPrice;
        int cumulativeBuyCost = getCumulativeBuyCost(customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NON_EXIST));

        int availableCash = customer.getAccountBalance();

        log.info("[매수 유효성 검증] 고객: {}, 기존 요청 금액: {}, 신규 요청 금액: {}, 총합: {}, 보유 현금: {}",
                customerId, cumulativeBuyCost, newBuyCost, cumulativeBuyCost + newBuyCost, availableCash);

        return availableCash >= (cumulativeBuyCost + newBuyCost);
    }

    private int getCumulativeBuyCost(Long customerId) {
        Set<RedisCustomerTradeValue> buyOrders = redisOrderRepository.getCustomerBuyOrders(customerId);

        if (buyOrders == null || buyOrders.isEmpty()) {
            log.info("[누적 매수 금액 조회] 데이터 없음 - customer:{}", customerId);
            return 0;
        }

        return buyOrders.stream()
                .filter(Objects::nonNull)
                .mapToInt(order -> order.getTokenAmount() * order.getTokenPrice())
                .sum();
    }

    @Override
    public void sell(SellEstateRequest request, Long customerId) {
        Long estateId = request.getEstateId();
        Integer sellAmount = request.getTradeTokenAmount();

        if (!isValidSellRequest(customerId, estateId, sellAmount)) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }

        // Kafka 연동은 추후 처리
        System.out.println("[매도 처리 완료] 고객: " + customerId + ", 부동산: " + estateId + ", 매도량: " + sellAmount);
    }

    private boolean isValidSellRequest(Long customerId, Long estateId, int newSellAmount) {
        int requestedSellAmount = getCumulativeSellAmount(customerId, estateId);
        int ownedAmount = accountRepository.findByCustomer_CustomerIdAndEstate_EstateId(customerId, estateId)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NON_EXIST))
                .getAccountTokenAmount();

        return ownedAmount + (requestedSellAmount + newSellAmount) >= 0;
    }

    private int getCumulativeSellAmount(Long customerId, Long estateId) {
        Set<RedisEstateTradeValue> sellOrders = redisOrderRepository.getEstateSellOrders(estateId);

        if (sellOrders == null || sellOrders.isEmpty()) {
            log.info("[누적 매도량 조회] 데이터 없음 - estate:{}, customer:{}", estateId, customerId);
            return 0;
        }

        return sellOrders.stream()
                .filter(order -> order.getCustomerId().equals(customerId) && order.getTokenAmount() < 0)
                .mapToInt(RedisEstateTradeValue::getTokenAmount)
                .sum();
    }
}