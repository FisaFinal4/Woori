package com.piehouse.woorepie.trade.service.implement;

import com.piehouse.woorepie.customer.entity.Account;
import com.piehouse.woorepie.customer.repository.AccountRepository;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import com.piehouse.woorepie.trade.dto.request.BuyEstateRequest;
import com.piehouse.woorepie.trade.dto.request.RedisEstateTradeValue;
import com.piehouse.woorepie.trade.dto.request.SellEstateRequest;
import com.piehouse.woorepie.trade.service.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TradeServiceImpl implements TradeService {

    private final RedisTemplate<String, RedisEstateTradeValue> redisEstateTradeTemplate;
    private final AccountRepository accountRepository;

    @Override
    public void buy(BuyEstateRequest request) {
        // 추후 구현 예정
        System.out.println("[매수 요청 도착]: " + request);
    }

    @Override
    public void sell(SellEstateRequest request, Long customerId) {
        Long estateId = request.getEstateId();
        Integer sellAmount = request.getTradeTokenAmount();

        if (!isValidSellRequest(customerId, estateId, sellAmount)) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }

        // Kafka 연동은 추후 처리
        System.out.println("[매도 처리 완료] 고객: " + customerId + ", 부동산: " + estateId + ", 매도량: " + sellAmount);
    }
    //매물 누적 매도 계산 로직
    private int getCumulativeSellAmount(Long customerId, Long estateId) {
        String key = "estate:" + estateId + ":sell";
        Set<ZSetOperations.TypedTuple<RedisEstateTradeValue>> entries = redisEstateTradeTemplate.opsForZSet().rangeWithScores(key, 0, -1);

        if (entries == null || entries.isEmpty()) {
            log.info("데이터 없음");
            return 0;
        }

        return entries.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .filter(value -> value.getCustomerId().equals(customerId) && value.getTokenAmount() < 0)
                .mapToInt(RedisEstateTradeValue::getTokenAmount)
                .sum();
    }
    // 매도 요청 유효성 검증
    private boolean isValidSellRequest(Long customerId, Long estateId, int newSellAmount) {
        int requestedSellAmount = getCumulativeSellAmount(customerId, estateId);
        Account account = accountRepository.findByCustomer_CustomerIdAndEstate_EstateId(customerId, estateId)
                .orElseThrow(() -> new CustomException(ErrorCode.TOKEN_NON_EXIST));
        int ownedAmount = account.getAccountTokenAmount();

        return ownedAmount + (requestedSellAmount + newSellAmount) >= 0;
    }
}
