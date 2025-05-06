package com.piehouse.woorepie.trade.controller;

import com.piehouse.woorepie.trade.dto.request.*;
import com.piehouse.woorepie.trade.service.TradeRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class TradeRedisController { // Redis 동작 테스트용 Controller

    private final TradeRedisService tradeRedisService;

    // 매수 주문 저장 (매물/고객 기준 동시에)
    @PostMapping("/buy")
    public String saveBuyOrder(@RequestBody BuyEstateRequest request, @RequestHeader("customerId") Long customerId) {
        tradeRedisService.saveBuyOrder(request.getEstateId(), customerId, request.getTradeTokenAmount(), request.getTradePrice());
        return "매수 주문 저장 완료";
    }

    // 매도 주문 저장 (매물/고객 기준 동시에)
    @PostMapping("/sell")
    public String saveSellOrder(@RequestBody SellEstateRequest request, @RequestHeader("customerId") Long customerId) {
        tradeRedisService.saveSellOrder(request.getEstateId(), customerId, request.getTradeTokenAmount(), request.getTradePrice());
        return "매도 주문 저장 완료";
    }

    // 매물 기준 매수 주문 전체 조회
    @GetMapping("/estate/{estateId}/buy")
    public Set<RedisEstateTradeValue> getEstateBuyOrders(@PathVariable Long estateId) {
        return tradeRedisService.getEstateBuyOrders(estateId);
    }

    // 매물 기준 매도 주문 전체 조회
    @GetMapping("/estate/{estateId}/sell")
    public Set<RedisEstateTradeValue> getEstateSellOrders(@PathVariable Long estateId) {
        return tradeRedisService.getEstateSellOrders(estateId);
    }

    // 고객 기준 매수 주문 전체 조회
    @GetMapping("/customer/buy")
    public Set<RedisCustomerTradeValue> getCustomerBuyOrders(@RequestHeader("customerId") Long customerId) {
        return tradeRedisService.getCustomerBuyOrders(customerId);
    }

    // 고객 기준 매도 주문 전체 조회
    @GetMapping("/customer/sell")
    public Set<RedisCustomerTradeValue> getCustomerSellOrders(@RequestHeader("customerId") Long customerId) {
        return tradeRedisService.getCustomerSellOrders(customerId);
    }

    // 매물 기준 가장 오래된 매수 주문 꺼내기 + 삭제 (고객 기준 동시 삭제)
    @GetMapping("/estate/{estateId}/buy/pop-oldest")
    public RedisEstateTradeValue popOldestBuyOrder(@PathVariable Long estateId) {
        return tradeRedisService.popOldestBuyOrderFromBoth(estateId);
    }

    // 매물 기준 가장 오래된 매도 주문 꺼내기 + 삭제 (고객 기준 동시 삭제)
    @GetMapping("/estate/{estateId}/sell/pop-oldest")
    public RedisEstateTradeValue popOldestSellOrder(@PathVariable Long estateId) {
        return tradeRedisService.popOldestSellOrderFromBoth(estateId);
    }
}