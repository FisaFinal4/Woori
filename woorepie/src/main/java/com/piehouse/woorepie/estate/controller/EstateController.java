package com.piehouse.woorepie.estate.controller;

import com.piehouse.woorepie.estate.dto.request.ModifyEstateRequest;
import com.piehouse.woorepie.estate.dto.response.GetEstateDetailsResponse;
import com.piehouse.woorepie.estate.dto.response.GetEstatePriceResponse;
import com.piehouse.woorepie.estate.dto.response.GetEstateSimpleResponse;
import com.piehouse.woorepie.estate.service.EstateService;
import com.piehouse.woorepie.global.response.ApiResponse;
import com.piehouse.woorepie.global.response.ApiResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estate")
@RequiredArgsConstructor
public class EstateController {

    private final EstateService estateService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<GetEstateSimpleResponse>>> getAllEstates(HttpServletRequest request) {
        List<GetEstateSimpleResponse> responses = estateService.getAllEstates();
        return ApiResponseUtil.success(responses, request);
    }

    //매물 상세정보 조회
    @GetMapping(params = "estateId")
    public ResponseEntity<ApiResponse<GetEstateDetailsResponse>> getEstateDetails(
            @RequestParam("estateId") Long estateId,
            HttpServletRequest request
    ) {
        GetEstateDetailsResponse details = estateService.getEstateDetails(estateId);
        return ApiResponseUtil.success(details, request);
    }


    // 매물 시세 내역 조회
    @GetMapping("/price")
    public ResponseEntity<ApiResponse<List<GetEstatePriceResponse>>> getPriceHistory(
            @RequestParam("estateId") Long estateId,
            HttpServletRequest request
    ) {
        List<GetEstatePriceResponse> history = estateService.getEstatePriceHistory(estateId);
        return ApiResponseUtil.success(history, request);
    }

    // 매물 수정
    @PatchMapping("/modify")
    public ResponseEntity<ApiResponse<String>> modifyEstate(
            @Valid @RequestBody ModifyEstateRequest request,
            HttpServletRequest httpRequest
    ) {
        estateService.modifyEstate(request);
        return ApiResponseUtil.success("매물 수정 완료", httpRequest);
    }


}
