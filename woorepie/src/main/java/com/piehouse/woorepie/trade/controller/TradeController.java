package com.piehouse.woorepie.trade.controller;

import com.piehouse.woorepie.customer.dto.SessionCustomer;
import com.piehouse.woorepie.global.response.ApiResponse;
import com.piehouse.woorepie.global.response.ApiResponseUtil;
import com.piehouse.woorepie.trade.dto.request.BuyEstateRequest;
import com.piehouse.woorepie.trade.dto.request.SellEstateRequest;
import com.piehouse.woorepie.trade.dto.request.CreateSubscriptionTradeRequest;
import com.piehouse.woorepie.trade.service.TradeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trade")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    /**
     * 매수 요청 (고객 로그인 필요)
     */
    @PostMapping("/buy")
    public ResponseEntity<ApiResponse<String>> buy(
            @RequestBody @Valid BuyEstateRequest request,
            @AuthenticationPrincipal SessionCustomer sessionCustomer,
            HttpServletRequest httpRequest
    ) {
        tradeService.buy(request, sessionCustomer.getCustomerId());
        return ApiResponseUtil.of(HttpStatus.OK, "매수 요청 성공", null, httpRequest);
    }

    /**
     * 매도 요청 (고객 로그인 필요)
     */
    @PostMapping("/sell")
    public ResponseEntity<ApiResponse<String>> sell(
            @RequestBody @Valid SellEstateRequest request,
            @AuthenticationPrincipal SessionCustomer sessionCustomer,
            HttpServletRequest httpRequest
    ) {
        tradeService.sell(request, sessionCustomer.getCustomerId());
        return ApiResponseUtil.of(HttpStatus.OK, "매도 요청 성공", null, httpRequest);
    }
    
    //청약 신청 성공
    @PostMapping("/subscription")
    public ResponseEntity<ApiResponse<Void>> subscribe(
            @RequestBody @Valid CreateSubscriptionTradeRequest request,
            @AuthenticationPrincipal SessionCustomer sessionCustomer,
            HttpServletRequest httpRequest
    ) {
        tradeService.createSubscription(request, sessionCustomer.getCustomerId());
        return ApiResponseUtil.of(HttpStatus.OK, "청약 요청 성공", null, httpRequest);
    }

}