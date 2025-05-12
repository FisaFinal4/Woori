package com.piehouse.woorepie.estate.service.implement;

import com.piehouse.woorepie.estate.dto.request.ModifyEstateRequest;
import com.piehouse.woorepie.estate.entity.RedisEstatePrice;
import com.piehouse.woorepie.estate.dto.response.GetEstateDetailsResponse;
import com.piehouse.woorepie.estate.dto.response.GetEstatePriceResponse;
import com.piehouse.woorepie.estate.dto.response.GetEstateSimpleResponse;
import com.piehouse.woorepie.estate.entity.Estate;
import com.piehouse.woorepie.estate.entity.EstatePrice;
import com.piehouse.woorepie.estate.repository.EstatePriceRepository;
import com.piehouse.woorepie.estate.repository.EstateRepository;
import com.piehouse.woorepie.estate.service.EstateService;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstateServiceImpl implements EstateService {

    private final RedisTemplate<String, Object> redisObjectTemplate;
    private final EstateRepository estateRepository;
    private final EstatePriceRepository estatePriceRepository;
    private static final String REDIS_ESTATE_PRICE_KEY_PREFIX = "estate:price:";

    @Override
    public List<GetEstateSimpleResponse> getTradableEstates() {
        List<Estate> estates = estateRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        return estates.stream()
                .filter(e -> e.getSubEndDate() != null && e.getSubEndDate().isBefore(now))
                .map(estate -> {
                    Long estateId = estate.getEstateId();
                    RedisEstatePrice price = getRedisEstatePrice(estateId);

                    return GetEstateSimpleResponse.builder()
                            .estateId(estateId)
                            .estateName(estate.getEstateName())
                            .estateCity(estate.getEstateCity())
                            .tokenAmount(price.getTokenAmount())
                            .estateTokenPrice(price.getEstateTokenPrice())
                            .estateRegistrationDate(estate.getEstateRegistrationDate())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public GetEstateDetailsResponse getTradableEstateDetails(Long estateId) {
        Estate estate = estateRepository.findById(estateId)
                .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));

        EstatePrice price = estatePriceRepository
                .findTopByEstate_EstateIdOrderByEstatePriceDateDesc(estateId)
                .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));

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
                .estateTokenPrice(price.getEstatePrice())
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



    @Override
    public RedisEstatePrice getRedisEstatePrice(Long estateId) {
        try {
            String key = REDIS_ESTATE_PRICE_KEY_PREFIX + estateId;
            ValueOperations<String, Object> ops = redisObjectTemplate.opsForValue();

            Object cached = ops.get(key);
            if (cached instanceof RedisEstatePrice price) {
                return price;
            }

            Estate estate = estateRepository.findById(estateId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));

            EstatePrice latest = estatePriceRepository
                    .findTopByEstate_EstateIdOrderByEstatePriceDateDesc(estateId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));

            int tokenCount = estate.getTokenAmount();
            int estatePrice = latest.getEstatePrice();
            int estateTokenPrice = tokenCount != 0 ? estatePrice / tokenCount : 0;

            RedisEstatePrice rep = RedisEstatePrice.builder()
                    .estatePrice(estatePrice)
                    .estateTokenPrice(estateTokenPrice)
                    .tokenAmount(tokenCount)
                    .build();

            ops.set(key, rep); // 캐싱
            return rep;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }
}
