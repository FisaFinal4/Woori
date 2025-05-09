package com.piehouse.woorepie.global.kafka.service.impliment;

import com.piehouse.woorepie.global.kafka.dto.OrderCreatedEvent;
import com.piehouse.woorepie.global.kafka.service.KafkaConsumerService;
import com.piehouse.woorepie.trade.service.TradeRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TradeRedisService tradeRedisService;

    @Override
    @KafkaListener(topics = "test", groupId = "group-test")
    public void listenToTopicTest(String message) {
        System.out.println("Received from topic-test: " + message);
    }

    @Override
    @KafkaListener(topics = "order.created", groupId = "group-order")
    public void consumeOrderCreated(OrderCreatedEvent event) {
        log.info("주문 생성 이벤트 수신: {}", event);
        if (event.getTradeTokenAmount() > 0) {
            // 매수 주문
            tradeRedisService.matchNewBuyOrder(event);
        } else if (event.getTradeTokenAmount() < 0) {
            // 매도 주문
            tradeRedisService.matchNewSellOrder(event);
        }
    }
}
