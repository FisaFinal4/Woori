package com.piehouse.woorepie.trade.service.implement;

import com.piehouse.woorepie.trade.dto.request.RedisCustomerTradeValue;
import com.piehouse.woorepie.trade.dto.request.RedisEstateTradeValue;
import com.piehouse.woorepie.trade.repository.RedisTradeRepository;
import com.piehouse.woorepie.trade.service.TradeRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class TradeRedisServiceImpl implements TradeRedisService {

    private final RedisTradeRepository redisRepository;

    // 매물과 고객 기준 매수 주문 동시 저장
    @Override
    public void saveBuyOrder(Long estateId, Long customerId, int tokenAmount, int tokenPrice) {
        long timestamp = System.currentTimeMillis();
        RedisEstateTradeValue estateBuyOrder = new RedisEstateTradeValue(customerId, tokenAmount, tokenPrice, timestamp);
        RedisCustomerTradeValue customerBuyOrder = new RedisCustomerTradeValue(estateId, tokenAmount, tokenPrice, timestamp);

        redisRepository.saveEstateBuyOrder(estateBuyOrder, estateId);
        redisRepository.saveCustomerBuyOrder(customerBuyOrder, customerId);
    }

    // 매물과 고객 기준 매도 주문 동시 저장
    @Override
    public void saveSellOrder(Long estateId, Long customerId, int tokenAmount, int tokenPrice) {
        long timestamp = System.currentTimeMillis();
        RedisEstateTradeValue estateSellOrder = new RedisEstateTradeValue(customerId, -tokenAmount, tokenPrice, timestamp);
        RedisCustomerTradeValue customerSellOrder = new RedisCustomerTradeValue(estateId, -tokenAmount, tokenPrice, timestamp);

        redisRepository.saveEstateSellOrder(estateSellOrder, estateId);
        redisRepository.saveCustomerSellOrder(customerSellOrder, customerId);
    }

    // 매물 기준 매수 주문 전체 조회 (시간순)
    @Override
    public Set<RedisEstateTradeValue> getEstateBuyOrders(Long estateId) {
        return redisRepository.getEstateBuyOrders(estateId);
    }

    // 매물 기준 매도 주문 전체 조회 (시간순)
    @Override
    public Set<RedisEstateTradeValue> getEstateSellOrders(Long estateId) {
        return redisRepository.getEstateSellOrders(estateId);
    }

    // 고객 기준 매수 주문 전체 조회 (시간순)
    @Override
    public Set<RedisCustomerTradeValue> getCustomerBuyOrders(Long customerId) {
        return redisRepository.getCustomerBuyOrders(customerId);
    }

    // 고객 기준 매도 주문 전체 조회 (시간순)
    @Override
    public Set<RedisCustomerTradeValue> getCustomerSellOrders(Long customerId) {
        return redisRepository.getCustomerSellOrders(customerId);
    }

    // 매물 기준 가장 먼저 들어온 매수 주문 꺼내기 + 고객 기준에서도 함께 삭제
    @Override
    public RedisEstateTradeValue popOldestBuyOrderFromBoth(Long estateId) {
        return redisRepository.popOldestBuyOrderFromBoth(estateId);
    }

    // 매물 기준 가장 먼저 들어온 매도 주문 꺼내기 + 고객 기준에서도 함께 삭제
    @Override
    public RedisEstateTradeValue popOldestSellOrderFromBoth(Long estateId) {
        return redisRepository.popOldestSellOrderFromBoth(estateId);
    }
}
