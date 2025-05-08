package com.piehouse.woorepie.estate.service.impliment;

import com.piehouse.woorepie.estate.dto.RedisEstatePrice;
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

@Service
@RequiredArgsConstructor
public class EstateServiceImpl implements EstateService {

    private final RedisTemplate<String, Object> redisObjectTemplate;
    private final EstateRepository estateRepository;
    private final EstatePriceRepository estatePriceRepository;
    private static final String REDIS_KEY_PREFIX = "estate:price:";

    // 매물 시세 redis 저장
    public RedisEstatePrice getRedisEstatePrice(Long estateId) {
        try {
            String key = REDIS_KEY_PREFIX + estateId;

            ValueOperations<String, Object> ops = redisObjectTemplate.opsForValue();

            // Redis에서 꺼내기
            Object cached = ops.get(key);
            if (cached instanceof RedisEstatePrice price) {
                return price;
            }

            // PostgreSQL에서 꺼내기
            Estate estate = estateRepository.findById(estateId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));

            EstatePrice latest = estatePriceRepository
                    .findTopByEstate_EstateIdOrderByEstatePriceDateDesc(estateId)
                    .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));

            // 발행 토큰 개수
            int tokenCount = estate.getTokenAmount();
            // 가장 최근 매물 시세
            int estatePrice = latest.getEstatePrice();
            // 토큰 당 가격
            int estateTokenPrice = tokenCount != 0 ? estatePrice / tokenCount : 0;

            RedisEstatePrice rep = RedisEstatePrice.builder()
                    .estatePrice(estatePrice)
                    .estateTokenPrice(estateTokenPrice)
                    .tokenAmount(tokenCount)
                    .build();

            //Redis에 저장
            ops.set(key, rep);

            return rep;
        }catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

}
