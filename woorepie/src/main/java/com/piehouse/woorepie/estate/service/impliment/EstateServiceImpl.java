package com.piehouse.woorepie.estate.service.impliment;

import com.piehouse.woorepie.estate.dto.RedisEstatePrice;
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
    public List<GetEstateSimpleResponse> getAllEstates() {
        List<Estate> estates = estateRepository.findAll();

        return estates.stream().map(estate -> {
            Long estateId = estate.getEstateId();

            RedisEstatePrice price = getRedisEstatePrice(estateId);

            return new GetEstateSimpleResponse(
                    estateId,
                    estate.getEstateName(),
                    estate.getEstateCity(),
                    price.getTokenAmount(),
                    price.getEstateTokenPrice(),
                    estate.getEstateRegistrationDate()
            );
        }).collect(Collectors.toList());
    }

    // Redis + PostgreSQL 기반 시세 조회
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
