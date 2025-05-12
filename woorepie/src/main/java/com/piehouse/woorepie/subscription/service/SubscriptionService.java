package com.piehouse.woorepie.subscription.service;

import com.piehouse.woorepie.agent.entity.Agent;
import com.piehouse.woorepie.agent.repository.AgentRepository;
import com.piehouse.woorepie.subscription.dto.request.RegisterEstateRequest;
import com.piehouse.woorepie.estate.entity.Estate;
import com.piehouse.woorepie.estate.entity.EstatePrice;
import com.piehouse.woorepie.estate.repository.EstatePriceRepository;
import com.piehouse.woorepie.estate.repository.EstateRepository;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final EstateRepository estateRepository;
    private final EstatePriceRepository estatePriceRepository;
    private final AgentRepository agentRepository;

    @Transactional
    public void registerEstate(RegisterEstateRequest request, Long agentId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        //매물 테이블 저장
        Estate estate = Estate.builder()
                .agent(agent)
                .estateName(request.getEstateName())
                .estateState(request.getEstateState())
                .estateCity(request.getEstateCity())
                .estateAddress(request.getEstateAddress())
                .estateLatitude(request.getEstateLatitude())
                .estateLongitude(request.getEstateLongitude())
                .estateDescription(request.getEstateDescription())
                .estateImageUrl(request.getEstateImageUrl())
                .subGuideUrl(request.getSubGuideUrl())
                .securitiesReportUrl(request.getSecuritiesReportUrl())
                .investmentExplanationUrl(request.getInvestmentExplanationUrl())
                .propertyMngContractUrl(request.getPropertyMngContractUrl())
                .appraisalReportUrl(request.getAppraisalReportUrl())
                .estateRegistrationDate(LocalDateTime.now())
                .tokenAmount(request.getTokenAmount())
                .build();

        estateRepository.save(estate);
        // 시세내역 테이블에도 저장
        EstatePrice estatePrice = EstatePrice.builder()
                .estate(estate)
                .estatePrice(request.getEstatePrice())
                .estatePriceDate(LocalDateTime.now())
                .build();

        estatePriceRepository.save(estatePrice);
    }
}
