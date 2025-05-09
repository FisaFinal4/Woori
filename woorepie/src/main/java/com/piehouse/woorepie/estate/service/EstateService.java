package com.piehouse.woorepie.estate.service;

import com.piehouse.woorepie.estate.dto.request.ModifyEstateRequest;
import com.piehouse.woorepie.estate.dto.response.GetEstateDetailsResponse;
import com.piehouse.woorepie.estate.dto.response.GetEstatePriceResponse;
import com.piehouse.woorepie.estate.dto.response.GetEstateSimpleResponse;
import com.piehouse.woorepie.estate.entity.Estate;
import com.piehouse.woorepie.estate.entity.EstatePrice;
import com.piehouse.woorepie.estate.repository.EstateRepository;
import com.piehouse.woorepie.estate.repository.EstatePriceRepository;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstateService {

    private final EstateRepository estateRepository;
    private final EstatePriceRepository estatePriceRepository;

    // 매물 리스트 조회
    public List<GetEstateSimpleResponse> getAllEstates() {
        List<Estate> estates = estateRepository.findAll(); // 정렬 필요시 `.findAll(Sort.by("estateRegistrationDate").descending())`

        return estates.stream()
                .map(e -> new GetEstateSimpleResponse(
                        e.getEstateId(),
                        e.getEstateName(),
                        e.getEstateCity(),
                        e.getTokenAmount(),
                        e.getEstateRegistrationDate()
                ))
                .collect(Collectors.toList());
    }

    public GetEstateDetailsResponse getEstateDetails(Long estateId) {
        Estate estate = estateRepository.findById(estateId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        // 가장 최신 가격 가져오기
        EstatePrice latestPrice = estatePriceRepository.findTopByEstate_EstateIdOrderByEstatePriceDateDesc(estateId)
                .orElse(null);

        return GetEstateDetailsResponse.builder()
                .estateId(estate.getEstateId())
                .agentId(estate.getAgent().getAgentId())
                .agentName(estate.getAgent().getAgentName())
                .estateName(estate.getEstateName())
                .estateAddress(estate.getEstateAddress())
                .tokenAmount(estate.getTokenAmount())
                .estateDescription(estate.getEstateDescription())
                .subGuideUrl(estate.getSubGuideUrl())
                .securitiesReportUrl(estate.getSecuritiesReportUrl())
                .investmentExplanationUrl(estate.getInvestmentExplanationUrl())
                .propertyMngContractUrl(estate.getPropertyMngContractUrl())
                .appraisalReportUrl(estate.getAppraisalReportUrl())
                .estatePrice(latestPrice != null ? latestPrice.getEstatePrice() : null)
                .build();
    }
    // 매물 시세내역 조회
    public List<GetEstatePriceResponse> getEstatePriceHistory(Long estateId) {
        List<EstatePrice> prices = estatePriceRepository
                .findAllByEstate_EstateIdOrderByEstatePriceDateDesc(estateId);

        return prices.stream()
                .map(p -> new GetEstatePriceResponse(p.getEstatePrice(), p.getEstatePriceDate()))
                .collect(Collectors.toList());
    }
    //매물 수정
    public void modifyEstate(ModifyEstateRequest request) {
        Estate estate = estateRepository.findById(request.getEstateId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESOURCE_NOT_FOUND));

        // 예: 로그인한 agent 권한 체크 필요시 여기에 추가

        estate.updateEstateDescription(request.getEstateDescription());
    }
}
