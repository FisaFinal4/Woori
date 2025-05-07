package com.piehouse.woorepie.trade.repository;

import com.piehouse.woorepie.trade.dto.request.RedisCustomerTradeValue;
import com.piehouse.woorepie.trade.dto.request.RedisEstateTradeValue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
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

    // 고객별 매수 주문 조회 (시간순)
    public Set<RedisCustomerTradeValue> getCustomerBuyOrders(Long customerId) {
        String key = String.format(CUSTOMER_BUY_KEY, customerId);
        return redisCustomerTradeTemplate.opsForZSet().rangeByScore(key, 0, Double.MAX_VALUE);
    }

    // 고객별 매도 주문 조회 (시간순)
    public Set<RedisCustomerTradeValue> getCustomerSellOrders(Long customerId) {
        String key = String.format(CUSTOMER_SELL_KEY, customerId);
        return redisCustomerTradeTemplate.opsForZSet().rangeByScore(key, 0, Double.MAX_VALUE);
    }

    // 매물 기준 가장 먼저 들어온 매수 주문 꺼내기 + 고객 기준에서도 함께 삭제
    public RedisEstateTradeValue popOldestBuyOrderFromBoth(Long estateId) {
        // 1. 매물 기준에서 가장 먼저 들어온 매수 주문 꺼내기 (자동 삭제됨)
        String estateKey = String.format(ESTATE_BUY_KEY, estateId);
        Set<ZSetOperations.TypedTuple<RedisEstateTradeValue>> popped =
                redisEstateTradeTemplate.opsForZSet().popMin(estateKey, 1);

        if (popped.isEmpty()) {
            return null;
        }

        // 2. 꺼낸 주문 정보 확인
        RedisEstateTradeValue estateBuyOrder = popped.iterator().next().getValue();
        Long customerId = estateBuyOrder.getCustomerId();
        long timestamp = estateBuyOrder.getTimestamp();

        // 3. 고객 기준에서 같은 타임스탬프를 가진 주문 삭제
        String customerKey = String.format(CUSTOMER_BUY_KEY, customerId);
        redisCustomerTradeTemplate.opsForZSet().removeRangeByScore(customerKey, timestamp, timestamp);

        return estateBuyOrder;
    }

    // 매물 기준 가장 먼저 들어온 매도 주문 꺼내기 + 고객 기준에서도 함께 삭제
    public RedisEstateTradeValue popOldestSellOrderFromBoth(Long estateId) {
        // 매수와 동일한 로직, 키만 sell로 변경
        String estateKey = String.format(ESTATE_SELL_KEY, estateId);
        Set<ZSetOperations.TypedTuple<RedisEstateTradeValue>> popped =
                redisEstateTradeTemplate.opsForZSet().popMin(estateKey, 1);

        if (popped.isEmpty()) {
            return null;
        }

        RedisEstateTradeValue estateSellOrder = popped.iterator().next().getValue();
        Long customerId = estateSellOrder.getCustomerId();
        long timestamp = estateSellOrder.getTimestamp();

        String customerKey = String.format(CUSTOMER_SELL_KEY, customerId);
        redisCustomerTradeTemplate.opsForZSet().removeRangeByScore(customerKey, timestamp, timestamp);

        return estateSellOrder;
    }

    // 부분 체결된 매도 주문 업데이트 (원래 타임스탬프 유지)
    public void updateSellOrderWithOriginalTimestamp(Long estateId, Long customerId,
                                                     RedisEstateTradeValue estateSellOrder,
                                                     RedisCustomerTradeValue customerSellOrder) {
        // 매물 기준 매도 주문 업데이트
        String estateKey = String.format(ESTATE_SELL_KEY, estateId);
        redisEstateTradeTemplate.opsForZSet().add(estateKey, estateSellOrder, estateSellOrder.getTimestamp());

        // 고객 기준 매도 주문 업데이트
        String customerKey = String.format(CUSTOMER_SELL_KEY, customerId);
        redisCustomerTradeTemplate.opsForZSet().add(customerKey, customerSellOrder, customerSellOrder.getTimestamp());
    }

    // 부분 체결된 매수 주문 업데이트 (원래 타임스탬프 유지)
    public void updateBuyOrderWithOriginalTimestamp(Long estateId, Long customerId,
                                                    RedisEstateTradeValue estateBuyOrder,
                                                    RedisCustomerTradeValue customerBuyOrder) {
        // 매물 기준 매수 주문 업데이트
        String estateKey = String.format(ESTATE_BUY_KEY, estateId);
        redisEstateTradeTemplate.opsForZSet().add(estateKey, estateBuyOrder, estateBuyOrder.getTimestamp());

        // 고객 기준 매수 주문 업데이트
        String customerKey = String.format(CUSTOMER_BUY_KEY, customerId);
        redisCustomerTradeTemplate.opsForZSet().add(customerKey, customerBuyOrder, customerBuyOrder.getTimestamp());
    }
}
