package com.piehouse.woorepie.estate.service.implement;

import com.piehouse.woorepie.estate.dto.RedisEstatePrice;
import com.piehouse.woorepie.estate.entity.Dividend;
import com.piehouse.woorepie.estate.entity.Estate;
import com.piehouse.woorepie.estate.entity.EstatePrice;
import com.piehouse.woorepie.estate.repository.DividendRepository;
import com.piehouse.woorepie.estate.repository.EstatePriceRepository;
import com.piehouse.woorepie.estate.repository.EstateRepository;
import com.piehouse.woorepie.estate.service.EstateRedisService;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EstateRedisServiceImpl implements EstateRedisService {
    private final RedisTemplate<String, String> redisStringTemplate;
    private final RedisTemplate<String, Object> redisObjectTemplate;
    private final EstateRepository estateRepository;
    private final EstatePriceRepository estatePriceRepository;
    private final DividendRepository dividendRepository;
    private static final String REDIS_ESTATE_PRICE_KEY_PREFIX = "estate:price:";

    private static final String REMAINING_TOKENS_KEY_FORMAT = "subscription:%s:remaining-tokens"; // estateId

    // 청약 오픈 시 PostgreSQL에서 tokenAmount를 읽어와 Redis에 초기화
    @Transactional(readOnly = true)
    public void initializeRemainingTokens(Long estateId) {
        // 1. DB에서 tokenAmount 조회
        Integer tokenAmount = estateRepository.findTokenAmountByEstateId(estateId)
                .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));

        // 2. Redis에 저장 (초기화)
        String key = String.format(REMAINING_TOKENS_KEY_FORMAT, estateId);
        redisStringTemplate.opsForValue().set(key, String.valueOf(tokenAmount));
    }

    // 남은 토큰 수량 Reids에 저장 (STRING)
    public void setRemainingTokens(String estateId, int remainingTokens) {
        String key = String.format(REMAINING_TOKENS_KEY_FORMAT, estateId);
        redisStringTemplate.opsForValue().set(key, String.valueOf(remainingTokens));
    }

    // 남은 토큰 수량 Redis에서 조회
    public int getRemainingTokens(String estateId) {
        String key = String.format(REMAINING_TOKENS_KEY_FORMAT, estateId);
        String value = redisStringTemplate.opsForValue().get(key);
        return value != null ? Integer.parseInt(value) : 0;
    }

    // 토큰 수량 감소 (원자적 연산)
    public Long decrementTokens(String estateId, int amount) {
        String key = String.format(REMAINING_TOKENS_KEY_FORMAT, estateId);
        return redisStringTemplate.opsForValue().decrement(key, amount);
    }

    // 토큰 수량 증가 (원자적 연산)
    public Long incrementTokens(String estateId, int amount) {
        String key = String.format(REMAINING_TOKENS_KEY_FORMAT, estateId);
        return redisStringTemplate.opsForValue().increment(key, amount);
    }

    // 레디스에서 매물 시세 정보 가져오기
    public RedisEstatePrice getRedisEstatePrice(Long estateId) {
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

        BigDecimal dividendYield = dividendRepository
                .findTopByEstate_EstateIdOrderByDividendYieldDateDesc(estateId)
                .map(Dividend::getDividendYield)
                .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));

        int tokenCount = estate.getTokenAmount();
        int estatePrice = latest.getEstatePrice();
        int estateTokenPrice = tokenCount != 0 ? estatePrice / tokenCount : 0;

        // Redis 저장 객체 생성
        RedisEstatePrice rep = RedisEstatePrice.builder()
                .estatePrice(estatePrice)
                .estateTokenPrice(estateTokenPrice)
                .tokenAmount(tokenCount)
                .dividendYield(dividendYield)
                .build();

        // Redis 캐싱 후 반환
        ops.set(key, rep);
        return rep;

    }
}
