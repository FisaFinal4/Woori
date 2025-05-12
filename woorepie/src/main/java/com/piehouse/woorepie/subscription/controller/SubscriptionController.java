package com.piehouse.woorepie.subscription.controller;

import com.piehouse.woorepie.agent.dto.SessionAgent;
import com.piehouse.woorepie.global.response.ApiResponse;
import com.piehouse.woorepie.global.response.ApiResponseUtil;
import com.piehouse.woorepie.subscription.dto.request.RegisterEstateRequest;
import com.piehouse.woorepie.subscription.service.SubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscription")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

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
}
