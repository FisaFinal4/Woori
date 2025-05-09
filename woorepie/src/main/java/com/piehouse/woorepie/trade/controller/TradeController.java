package com.piehouse.woorepie.trade.controller;

import com.piehouse.woorepie.customer.dto.SessionCustomer;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import com.piehouse.woorepie.global.response.ApiResponse;
import com.piehouse.woorepie.trade.dto.request.BuyEstateRequest;
import com.piehouse.woorepie.trade.dto.request.SellEstateRequest;
import com.piehouse.woorepie.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trade")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    @PostMapping("/buy")
    public ResponseEntity<ApiResponse<String>> buy(
            @RequestBody BuyEstateRequest request
    ) {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof SessionCustomer sc)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        tradeService.buy(request, sc.getCustomerId());

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "매수 요청이 성공적으로 접수되었습니다.",
                        "/trade/buy",
                        null
                )
        );
    }

    @PostMapping("/sell")
    public ResponseEntity<ApiResponse<String>> sell(
            @RequestBody SellEstateRequest request
    ) {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof SessionCustomer sc)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        tradeService.sell(request, sc.getCustomerId());

        return ResponseEntity.ok(
                ApiResponse.of(
                        200,
                        "매도 요청이 성공적으로 접수되었습니다.",
                        "/trade/sell",
                        null
                )
        );
    }
}
