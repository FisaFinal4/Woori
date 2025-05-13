package com.piehouse.woorepie.estate.service.implement;

import com.piehouse.woorepie.estate.service.EstateRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EstateRedisServiceImpl implements EstateRedisService {
    private final RedisTemplate<String, String> redisStringTemplate;

    private static final String REMAINING_TOKENS_KEY_FORMAT = "subscription:%s:remaining-tokens"; // estateId

    // 1. 남은 토큰 수량 저장 (STRING)
    public void setRemainingTokens(String estateId, int remainingTokens) {
        String key = String.format(REMAINING_TOKENS_KEY_FORMAT, estateId);
        redisStringTemplate.opsForValue().set(key, String.valueOf(remainingTokens));
    }

    // 2. 남은 토큰 수량 조회
    public int getRemainingTokens(String estateId) {
        String key = String.format(REMAINING_TOKENS_KEY_FORMAT, estateId);
        String value = redisStringTemplate.opsForValue().get(key);
        return value != null ? Integer.parseInt(value) : 0;
    }

    // 3. 토큰 수량 감소 (원자적 연산)
    public Long decrementTokens(String estateId, int amount) {
        String key = String.format(REMAINING_TOKENS_KEY_FORMAT, estateId);
        return redisStringTemplate.opsForValue().decrement(key, amount);
    }

    // 4. 토큰 수량 증가 (원자적 연산)
    public Long incrementTokens(String estateId, int amount) {
        String key = String.format(REMAINING_TOKENS_KEY_FORMAT, estateId);
        return redisStringTemplate.opsForValue().increment(key, amount);
    }
}
