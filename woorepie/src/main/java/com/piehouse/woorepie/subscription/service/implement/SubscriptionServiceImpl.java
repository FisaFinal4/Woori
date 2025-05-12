package com.piehouse.woorepie.subscription.service.implement;

import com.piehouse.woorepie.agent.entity.Agent;
import com.piehouse.woorepie.agent.repository.AgentRepository;
import com.piehouse.woorepie.estate.dto.RedisEstatePrice;
import com.piehouse.woorepie.estate.entity.Estate;
import com.piehouse.woorepie.estate.entity.EstatePrice;
import com.piehouse.woorepie.estate.repository.EstatePriceRepository;
import com.piehouse.woorepie.estate.repository.EstateRepository;
import com.piehouse.woorepie.estate.service.EstateService;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import com.piehouse.woorepie.subscription.dto.request.RegisterEstateRequest;
import com.piehouse.woorepie.subscription.dto.response.GetSubscriptionDetailsResponse;
import com.piehouse.woorepie.subscription.dto.response.GetSubscriptionSimpleResponse;
import com.piehouse.woorepie.subscription.entity.Subscription;
import com.piehouse.woorepie.subscription.repository.SubscriptionRepository;
import com.piehouse.woorepie.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final EstateRepository estateRepository;
    private final EstatePriceRepository estatePriceRepository;
    private final AgentRepository agentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final EstateService estateService;

    @Override
    @Transactional
    public void registerEstate(RegisterEstateRequest request, Long agentId) {
        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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

        EstatePrice estatePrice = EstatePrice.builder()
                .estate(estate)
                .estatePrice(request.getEstatePrice())
                .estatePriceDate(LocalDateTime.now())
                .build();

        estatePriceRepository.save(estatePrice);
    }

    // 청약 매물 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public List<GetSubscriptionSimpleResponse> getActiveSubscriptions() {
        List<Subscription> subs = subscriptionRepository.findBySubState((short) 1);

        return subs.stream().map(sub -> {
            Estate estate = sub.getEstate();
            RedisEstatePrice price = estateService.getRedisEstatePrice(estate.getEstateId());

            return GetSubscriptionSimpleResponse.builder()
                    .estateId(estate.getEstateId())
                    .estateName(estate.getEstateName())
                    .agentName(estate.getAgent().getAgentName())
                    .subStartDate(sub.getSubDate())
                    .estateState(estate.getEstateState())
                    .estateCity(estate.getEstateCity())
                    .estateImageUrl(estate.getEstateImageUrl())
                    .tokenAmount(price.getTokenAmount())
                    .estatePrice(price.getEstatePrice())
                    .build();
        }).collect(Collectors.toList());
    }

    // 청약 매물 상세정보 조회
    @Override
    @Transactional(readOnly = true)
    public GetSubscriptionDetailsResponse getSubscriptionDetails(Long estateId) {
        Estate estate = estateRepository.findById(estateId)
                .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));

        EstatePrice price = estatePriceRepository
                .findTopByEstate_EstateIdOrderByEstatePriceDateDesc(estateId)
                .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));

        int subTokenAmount = estate.getTokenAmount();

        return GetSubscriptionDetailsResponse.builder()
                .estateId(estate.getEstateId())
                .estateName(estate.getEstateName())
                .agentId(estate.getAgent().getAgentId())
                .agentName(estate.getAgent().getAgentName())
                .subStartDate(estate.getSubStartDate())
                .subEndDate(estate.getSubEndDate())
                .estateAddress(estate.getEstateAddress())
                .estateImageUrl(estate.getEstateImageUrl())
                .estatePrice(price.getEstatePrice())
                .tokenAmount(estate.getTokenAmount())
                .subTokenAmount(subTokenAmount)
                .investmentExplanationUrl(estate.getInvestmentExplanationUrl())
                .propertyMngContractUrl(estate.getPropertyMngContractUrl())
                .appraisalReportUrl(estate.getAppraisalReportUrl())
                .build();
    }
}
