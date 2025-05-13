package com.piehouse.woorepie.estate.service;

public interface EstateRedisService {
    // 1. 남은 토큰 수량 저장 (STRING)
    public void setRemainingTokens(String estateId, int remainingTokens);

    // 2. 남은 토큰 수량 조회
    public int getRemainingTokens(String estateId);

    // 3. 토큰 수량 감소 (원자적 연산)
    public Long decrementTokens(String estateId, int amount);

    // 4. 토큰 수량 증가 (원자적 연산)
    public Long incrementTokens(String estateId, int amount);
}
