package com.piehouse.woorepie.trade.service;

import com.piehouse.woorepie.trade.dto.request.RedisCustomerTradeValue;
import com.piehouse.woorepie.trade.dto.request.RedisEstateTradeValue;

import java.util.Set;

public interface TradeRedisService {
    // 매물과 고객 기준 매수 주문 동시 저장
    void saveBuyOrder(Long estateId, Long customerId, int tokenAmount, int tokenPrice);

    // 매물과 고객 기준 매도 주문 동시 저장
    void saveSellOrder(Long estateId, Long customerId, int tokenAmount, int tokenPrice);

    // 매물 기준 매수 주문 전체 조회 (시간순)
    Set<RedisEstateTradeValue> getEstateBuyOrders(Long estateId);

    // 매물 기준 매도 주문 전체 조회 (시간순)
    Set<RedisEstateTradeValue> getEstateSellOrders(Long estateId);

    // 고객 기준 매수 주문 전체 조회 (시간순)
    Set<RedisCustomerTradeValue> getCustomerBuyOrders(Long customerId);

    // 고객 기준 매도 주문 전체 조회 (시간순)
    Set<RedisCustomerTradeValue> getCustomerSellOrders(Long customerId);

    // 매물 기준 가장 먼저 들어온 매수 주문 꺼내기 + 고객 기준에서도 함께 삭제
    RedisEstateTradeValue popOldestBuyOrderFromBoth(Long estateId);

    // 매물 기준 가장 먼저 들어온 매도 주문 꺼내기 + 고객 기준에서도 함께 삭제
    RedisEstateTradeValue popOldestSellOrderFromBoth(Long estateId);

    // 새 매수 주문이 들어왔을 때 호출
    void matchNewBuyOrder(Long estateId, Long customerId, int tokenAmount, int tokenPrice);

    // 새 매도 주문이 들어왔을 때 호출
    void matchNewSellOrder(Long estateId, Long customerId, int tokenAmount, int tokenPrice);

    // 반복 매칭 메소드
    void matchAllPossibleOrders(Long estateId);
}
