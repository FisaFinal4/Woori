package com.piehouse.woorepie.trade.controller;

import com.piehouse.woorepie.customer.dto.SessionCustomer;
import com.piehouse.woorepie.global.response.ApiResponse;
import com.piehouse.woorepie.trade.dto.request.BuyEstateRequest;
import com.piehouse.woorepie.trade.dto.request.SellEstateRequest;
import com.piehouse.woorepie.trade.service.TradeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trade")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("/buy")
    public ResponseEntity<ApiResponse<String>> buy(@RequestBody BuyEstateRequest request,
                                                   @AuthenticationPrincipal SessionCustomer sessionCustomer,
                                                   HttpServletRequest httpRequest) {
        Long customerId = sessionCustomer.getCustomerId();
        tradeService.buy(request, customerId);

        ApiResponse<String> response = ApiResponse.of(
                200,
                "매수 요청이 성공적으로 접수되었습니다.",
                httpRequest.getRequestURI(),
                null
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sell")
    public ResponseEntity<ApiResponse<String>> sell(@RequestBody SellEstateRequest request,
                                                    @AuthenticationPrincipal SessionCustomer sessionCustomer,
                                                    HttpServletRequest httpRequest) {
        Long customerId = sessionCustomer.getCustomerId();
        tradeService.sell(request, customerId);

        ApiResponse<String> response = ApiResponse.of(
                200,
                "매도 요청이 성공적으로 접수되었습니다.",
                httpRequest.getRequestURI(),
                null
        );
        return ResponseEntity.ok(response);
    }
}
