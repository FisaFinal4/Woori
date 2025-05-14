package com.piehouse.woorepie.subscription.service.implement;

import com.piehouse.woorepie.agent.entity.Agent;
import com.piehouse.woorepie.agent.repository.AgentRepository;
import com.piehouse.woorepie.estate.dto.RedisEstatePrice;
import com.piehouse.woorepie.estate.entity.Dividend;
import com.piehouse.woorepie.estate.entity.Estate;
import com.piehouse.woorepie.estate.entity.EstatePrice;
import com.piehouse.woorepie.estate.entity.SubState;
import com.piehouse.woorepie.estate.repository.DividendRepository;
import com.piehouse.woorepie.estate.repository.EstatePriceRepository;
import com.piehouse.woorepie.estate.repository.EstateRepository;
import com.piehouse.woorepie.estate.service.implement.EstateRedisServiceImpl;
import com.piehouse.woorepie.estate.service.implement.EstateServiceImpl;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import com.piehouse.woorepie.global.service.implement.S3ServiceImpl;
import com.piehouse.woorepie.subscription.dto.request.RegisterEstateRequest;
import com.piehouse.woorepie.subscription.dto.response.GetSubscriptionDetailsResponse;
import com.piehouse.woorepie.subscription.dto.response.GetSubscriptionSimpleResponse;
import com.piehouse.woorepie.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final EstateRepository estateRepository;
    private final EstatePriceRepository estatePriceRepository;
    private final AgentRepository agentRepository;
    private final EstateServiceImpl estateServiceImpl;
    private final EstateRedisServiceImpl  estateRedisServiceImpl;
    private final DividendRepository dividendRepository;
    private final S3ServiceImpl s3serviceImpl;

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
                .estateImageUrl(s3serviceImpl.getPublicS3Url(request.getEstateImageUrlKey()))
                .subGuideUrl(s3serviceImpl.getPublicS3Url(request.getSubGuideUrlKey()))
                .securitiesReportUrl(s3serviceImpl.getPublicS3Url(request.getSecuritiesReportUrlKey()))
                .investmentExplanationUrl(s3serviceImpl.getPublicS3Url(request.getInvestmentExplanationUrlKey()))
                .propertyMngContractUrl(s3serviceImpl.getPublicS3Url(request.getPropertyMngContractUrlKey()))
                .appraisalReportUrl(s3serviceImpl.getPublicS3Url(request.getAppraisalReportUrlKey()))
                .estateRegistrationDate(LocalDateTime.now())
                .tokenAmount(request.getTokenAmount())
                .build();
        estateRepository.save(estate);

    //매물 시세 테이블
        EstatePrice estatePrice = EstatePrice.builder()
                .estate(estate)
                .estatePrice(request.getEstatePrice())
                .estatePriceDate(LocalDateTime.now())
                .build();
        estatePriceRepository.save(estatePrice);

    //배당률 테이블도 함께 저장
        Dividend dividend = Dividend.builder()
                .estate(estate)
                .dividendYield(request.getDividendYield())
                .dividendYieldDate(LocalDateTime.now())
                .build();
        dividendRepository.save(dividend);

    }
    
    //청약 가능한 매물 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public List<GetSubscriptionSimpleResponse> getActiveSubscriptions() {

        List<Estate> estates = estateRepository.findBySubStateIn(List.of(
                SubState.READY, SubState.RUNNING, SubState.PENDING, SubState.FAILURE
        )); // 청약 중인 substate 필터링

        List<Long> estateIds = estates.stream()
                .map(Estate::getEstateId)
                .toList();

        Map<Long, RedisEstatePrice> estatePriceMap = estateRedisServiceImpl.getMultipleRedisEstatePrice(estateIds);


        return estates.stream()
                .map(estate -> {
                    RedisEstatePrice price = estatePriceMap.get(estate.getEstateId());

                    return GetSubscriptionSimpleResponse.builder()
                            .estateId(estate.getEstateId())
                            .estateName(estate.getEstateName())
                            .agentName(estate.getAgent().getAgentName())
                            .subStartDate(estate.getSubStartDate())
                            .estateState(estate.getEstateState())
                            .estateCity(estate.getEstateCity())
                            .estateImageUrl(estate.getEstateImageUrl())
                            .tokenAmount(price.getTokenAmount())
                            .estatePrice(price.getEstatePrice())
                            .build();
                })
                .collect(Collectors.toList());

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
