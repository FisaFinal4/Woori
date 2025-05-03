package com.piehouse.woorepie.trade.service.implement;

import com.piehouse.woorepie.customer.entity.Account;
import com.piehouse.woorepie.customer.repository.AccountRepository;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import com.piehouse.woorepie.trade.dto.request.BuyEstateRequest;
import com.piehouse.woorepie.trade.dto.request.SellEstateRequest;
import com.piehouse.woorepie.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final StringRedisTemplate redisTemplate;
    private final AccountRepository accountRepository;

    @Override
    public void buy(BuyEstateRequest request) {
        // 추후 구현 예정
        System.out.println("[매수 요청 도착]: " + request);
    }
    
    // 사용자 매도 요청
    @Override
    public void sell(SellEstateRequest request, Long customerId) {
//        Long customerId = request.getCustomerId();
        Long estateId = request.getEstateId();
        Integer sellAmount = request.getTradeTokenAmount();

//          확인용
//        if (!isValidJsonForm(request)) {
//            throw new CustomException(ErrorCode.INTERNAL_ERROR);
//        }
        if (!isValidSellRequest(customerId, estateId, sellAmount)) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
        
        // Kafka 연동은 추후 처리
        System.out.println("[매도 처리 완료] 고객: " + customerId + ", 부동산: " + estateId + ", 매도량: " + sellAmount);
    }
    
    //Redis 연동 확인용
    @Override
    public void testRedisConnection() {
        redisTemplate.opsForValue().set("test-key", "Hello Redis!");
        String value = redisTemplate.opsForValue().get("test-key");
        System.out.println("Redis Test Value: " + value);
    }

    // reids에서 현재 매도 누적합 (건물, 사용자 기준)
    private int getCumulativeSellAmount(Long customerId, Long estateId) {
        String key = "trade:request:estate:" + estateId;
        log.info(key + ": " + customerId);

        Set<String> entries = redisTemplate.opsForZSet().range(key, 0, -1);
        if (entries == null || entries.isEmpty()) {
            log.info("데이터 없음");
            return 0;
        }

        log.info("entry 사이즈: " + entries.size());
        return entries.stream()
                .filter(e -> e.startsWith(customerId + ":"))
                .map(e -> e.split(":")[1])
                .filter(val -> val.startsWith("-"))  // 매도 요청만 필터링
                .mapToInt(Integer::parseInt)
                .sum();
    }

    // 요청 유효성 확인 로직
    private boolean isValidSellRequest(Long customerId, Long estateId, int newSellAmount) {
        // 1. Redis: 이전 매도 요청 합
        int requestedSellAmount = getCumulativeSellAmount(customerId, estateId);
        log.info(requestedSellAmount + "");
        log.info(requestedSellAmount + newSellAmount + "");
        log.info("----------------------------------------------------");

        // 2. PostgreSQL: 실제 보유 토큰 수
        Account account = accountRepository.findByCustomer_CustomerIdAndEstate_EstateId(customerId, estateId)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NON_EXIST));
        int ownedAmount = account.getAccountTokenAmount();

        // 3. 유효성 판단
        return ownedAmount + (requestedSellAmount + newSellAmount) >= 0 ;
    }

    private boolean isValidJsonForm(SellEstateRequest request ) {
        return true;
    }
}
