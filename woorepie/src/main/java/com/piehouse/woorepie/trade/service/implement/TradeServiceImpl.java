package com.piehouse.woorepie.trade.service.implement;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.piehouse.woorepie.trade.dto.request.BuyEstateRequest;
import com.piehouse.woorepie.trade.dto.request.SellEstateRequest;
import com.piehouse.woorepie.trade.service.TradeService;

@RequiredArgsConstructor
@Service
public class TradeServiceImpl implements TradeService {

    private final StringRedisTemplate redisTemplate;  // 🔧 필드 추가

    @Override
    public void buy(BuyEstateRequest request) {
        // 매수 로직 작성 예정
        System.out.println("매수 요청 처리 중: " + request);
    }

    @Override
    public void sell(SellEstateRequest request) {
        // 매도 로직 작성 예정
        System.out.println("매도 요청 처리 중: " + request);
    }

    public void testRedisConnection() {
        redisTemplate.opsForValue().set("test-key", "Hello Redis!");
        String value = redisTemplate.opsForValue().get("test-key");
        System.out.println("Redis에서 가져온 값: " + value);
    }

    public String getUserOrder(String userId) {
        String key = "order:user:" + userId;
        return redisTemplate.opsForValue().get(key);
    }

}
