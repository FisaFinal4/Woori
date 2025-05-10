package com.piehouse.woorepie.subscription.controller;

import com.piehouse.woorepie.agent.dto.SessionAgent;
import com.piehouse.woorepie.global.response.ApiResponse;
import com.piehouse.woorepie.subscription.dto.request.RegisterEstateRequest;
import com.piehouse.woorepie.subscription.dto.response.GetSubscriptionDetailsResponse;
import com.piehouse.woorepie.subscription.dto.response.GetSubscriptionSimpleResponse;
import com.piehouse.woorepie.subscription.service.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    /**
     * 매물 청약 등록 (중개인 로그인 필요)
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> registerEstate(@RequestBody RegisterEstateRequest request,
                                                              @SessionAttribute(name = "loginAgent") SessionAgent sessionAgent,
                                                              HttpServletRequest httpRequest) {
        subscriptionService.registerEstate(request, sessionAgent.getAgentId());
        return ResponseEntity.status(201).body(ApiResponse.of(
                201,
                "매물 청약 등록 성공",
                httpRequest.getRequestURI(),
                null
        ));
    }

    /**
     * 청약중인 매물 리스트 조회
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<GetSubscriptionSimpleResponse>>> getAllSubscriptionEstates(
            HttpServletRequest request) {

        List<GetSubscriptionSimpleResponse> responseList = subscriptionService.getActiveSubscriptions();
        return ResponseEntity.ok(
                ApiResponse.of(200, "청약중인 매물 리스트 조회 성공", request.getRequestURI(), responseList)
        );
    }

    /**
     * 청약 매물 상세 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<GetSubscriptionDetailsResponse>> getSubscriptionDetails(
            @RequestParam Long estateId,
            HttpServletRequest request) {

        GetSubscriptionDetailsResponse response = subscriptionService.getSubscriptionDetails(estateId);
        return ResponseEntity.ok(
                ApiResponse.of(200, "청약 매물 상세 조회 성공", request.getRequestURI(), response)
        );
    }
}
