package com.piehouse.woorepie.trade.repository;

import com.piehouse.woorepie.trade.dto.request.RedisCustomerTradeValue;
import com.piehouse.woorepie.trade.dto.request.RedisEstateTradeValue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisTradeRepository {

    private final RedisTemplate<String, RedisEstateTradeValue> redisEstateTradeTemplate;
    private final RedisTemplate<String, RedisCustomerTradeValue> redisCustomerTradeTemplate;

    // 키 패턴 상수
    private static final String ESTATE_BUY_KEY = "estate:%d:buy";
    private static final String ESTATE_SELL_KEY = "estate:%d:sell";
    private static final String CUSTOMER_BUY_KEY = "customer:%d:buy";
    private static final String CUSTOMER_SELL_KEY = "customer:%d:sell";

    // 매물 기준 매수 주문 저장
    public void saveEstateBuyOrder(RedisEstateTradeValue order, Long estateId) {
        String key = String.format(ESTATE_BUY_KEY, estateId);
        redisEstateTradeTemplate.opsForZSet().add(key, order, order.getTimestamp());
    }

    // 매물 기준 매도 주문 저장
    public void saveEstateSellOrder(RedisEstateTradeValue order, Long estateId) {
        String key = String.format(ESTATE_SELL_KEY, estateId);
        redisEstateTradeTemplate.opsForZSet().add(key, order, order.getTimestamp());
    }

    // 고객 기준 매수 주문 저장
    public void saveCustomerBuyOrder(RedisCustomerTradeValue order, Long customerId) {
        String key = String.format(CUSTOMER_BUY_KEY, customerId);
        redisCustomerTradeTemplate.opsForZSet().add(key, order, order.getTimestamp());
    }

    // 고객 기준 매도 주문 저장
    public void saveCustomerSellOrder(RedisCustomerTradeValue order, Long customerId) {
        String key = String.format(CUSTOMER_SELL_KEY, customerId);
        redisCustomerTradeTemplate.opsForZSet().add(key, order, order.getTimestamp());
    }

    // 매물별 매수 주문 조회 (시간순)
    public Set<RedisEstateTradeValue> getEstateBuyOrders(Long estateId) {
        String key = String.format(ESTATE_BUY_KEY, estateId);
        return redisEstateTradeTemplate.opsForZSet().rangeByScore(key, 0, Double.MAX_VALUE);
    }

    // 매물별 매도 주문 조회 (시간순)
    public Set<RedisEstateTradeValue> getEstateSellOrders(Long estateId) {
        String key = String.format(ESTATE_SELL_KEY, estateId);
        return redisEstateTradeTemplate.opsForZSet().rangeByScore(key, 0, Double.MAX_VALUE);
    }

    // 매물 기준 주문 삭제
    public void removeEstateOrder(String key, RedisEstateTradeValue order) {
        redisEstateTradeTemplate.opsForZSet().remove(key, order);
    }

    // 고객 기준 주문 삭제
    public void removeCustomerOrder(String key, RedisCustomerTradeValue order) {
        redisCustomerTradeTemplate.opsForZSet().remove(key, order);
    }
}
