package com.piehouse.woorepie.trade.controller;

import com.piehouse.woorepie.trade.dto.request.BuyEstateRequest;
import com.piehouse.woorepie.trade.dto.request.SellEstateRequest;
import com.piehouse.woorepie.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trade")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("/buy")
    public ResponseEntity<String> buy(@RequestBody BuyEstateRequest request) {
        tradeService.buy(request);
        return ResponseEntity.ok("매수 요청 완료");
    }

    @PostMapping("/sell")
    public ResponseEntity<String> sell(@RequestBody SellEstateRequest request) {
        tradeService.sell(request);
        return ResponseEntity.ok("매도 요청 완료");
    }

    @GetMapping("/test-redis")
    public ResponseEntity<String> testRedis() {
        tradeService.testRedisConnection();
        return ResponseEntity.ok("Redis 테스트 완료");
    }

}
