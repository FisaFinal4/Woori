package com.piehouse.woorepie.estate.service.implement;

import com.piehouse.woorepie.estate.dto.request.ModifyEstateRequest;
import com.piehouse.woorepie.estate.dto.RedisEstatePrice;
import com.piehouse.woorepie.estate.dto.response.GetEstateDetailsResponse;
import com.piehouse.woorepie.estate.dto.response.GetEstatePriceResponse;
import com.piehouse.woorepie.estate.dto.response.GetEstateSimpleResponse;
import com.piehouse.woorepie.estate.entity.Estate;
import com.piehouse.woorepie.estate.entity.EstatePrice;
import com.piehouse.woorepie.estate.entity.SubState;
import com.piehouse.woorepie.estate.repository.EstatePriceRepository;
import com.piehouse.woorepie.estate.repository.EstateRepository;
import com.piehouse.woorepie.estate.service.EstateService;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstateServiceImpl implements EstateService {

    private final RedisTemplate<String, Object> redisObjectTemplate;
    private final EstateRepository estateRepository;
    private final EstatePriceRepository estatePriceRepository;
    private final EstateRedisServiceImpl estateRedisServiceImpl;

    // 매물 리스트 조회
    @Override
    public List<GetEstateSimpleResponse> getTradableEstates() {
        List<Estate> estates = estateRepository.findBySubState(SubState.SUCCESS);

        return estates.stream()
                .map(estate -> {
                    Long estateId = estate.getEstateId();
                    RedisEstatePrice price = estateRedisServiceImpl.getRedisEstatePrice(estateId);

                    return GetEstateSimpleResponse.builder()
                            .estateId(estateId)
                            .estateName(estate.getEstateName())
                            .estateState(estate.getEstateState())
                            .estateCity(estate.getEstateCity())
                            .tokenAmount(price.getTokenAmount())
                            .estateTokenPrice(price.getEstateTokenPrice())
                            .dividendYield(price.getDividendYield()) // BigDecimal 기준
                            .estateRegistrationDate(estate.getEstateRegistrationDate())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // 매물 상세내역 조회
    @Override
    public GetEstateDetailsResponse getTradableEstateDetails(Long estateId) {
        Estate estate = estateRepository.findById(estateId)
                .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));

        RedisEstatePrice price = estateRedisServiceImpl.getRedisEstatePrice(estateId);

        return GetEstateDetailsResponse.builder()
                .estateId(estate.getEstateId())
                .agentId(estate.getAgent().getAgentId())
                .agentName(estate.getAgent().getAgentName())
                .estateName(estate.getEstateName())
                .estateState(estate.getEstateState())
                .estateCity(estate.getEstateCity())
                .estateAddress(estate.getEstateAddress())
                .estateLatitude(estate.getEstateLatitude())
                .estateLongitude(estate.getEstateLongitude())
                .tokenAmount(estate.getTokenAmount())
                .estateDescription(estate.getEstateDescription())
                .totalEstateArea(estate.getTotalEstateArea())
                .tradedEstateArea(estate.getTradedEstateArea())
                .subGuideUrl(estate.getSubGuideUrl())
                .securitiesReportUrl(estate.getSecuritiesReportUrl())
                .investmentExplanationUrl(estate.getInvestmentExplanationUrl())
                .propertyMngContractUrl(estate.getPropertyMngContractUrl())
                .appraisalReportUrl(estate.getAppraisalReportUrl())
                .estateTokenPrice(price.getEstateTokenPrice())
                .dividendYield(price.getDividendYield())
                .build();
    }


    @Override
    @Transactional(readOnly = true)
    public List<GetEstatePriceResponse> getEstatePriceHistory(Long estateId) {
        // 1. 매물 존재 여부 확인
        Estate estate = estateRepository.findById(estateId)
                .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));

        // 2. 시세 내역 조회
        List<EstatePrice> priceList = estatePriceRepository
                .findAllByEstate_EstateIdOrderByEstatePriceDateDesc(estateId);

        if (priceList.isEmpty()) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND); // 시세 없음
        }

        // 3. 응답 DTO 변환
        return priceList.stream()
                .map(p -> GetEstatePriceResponse.builder()
                        .estatePrice(p.getEstatePrice())
                        .estatePriceDate(p.getEstatePriceDate())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void modifyEstateDescription(Long agentId, ModifyEstateRequest request) {
        Estate estate = estateRepository.findById(request.getEstateId())
                .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));
        estate.updateDescription(request.getEstateDescription());
    }


}
