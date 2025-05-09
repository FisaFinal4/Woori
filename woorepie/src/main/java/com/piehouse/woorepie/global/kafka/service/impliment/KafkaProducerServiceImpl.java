package com.piehouse.woorepie.global.kafka.service.impliment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.piehouse.woorepie.global.kafka.dto.TransactionCreatedEvent;
import com.piehouse.woorepie.global.kafka.dto.OrderCreatedEvent;
import com.piehouse.woorepie.global.kafka.service.KafkaProducerService;
import com.piehouse.woorepie.global.util.KafkaRetryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final KafkaRetryUtil kafkaRetryUtil;

    private static final String TEST_TOPIC = "test";
    private static final String ORDER_CREATED_TOPIC = "order.created";
    private static final String TRANSACTION_CREATED_TOPIC = "transaction.created";


    @Override
    public void sendToTopicTest(String message) {
        kafkaTemplate.send(TEST_TOPIC, message);
    }

    // Kafka에 매수, 매도 요청 이벤트 보내기
    @Override
    public void sendOrderCreated(OrderCreatedEvent event) {
        kafkaTemplate.send(ORDER_CREATED_TOPIC, event) // 객체 직접 전송
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Kafka 전송 성공: orderId={}", event.getEstateId());
                    } else {
                        kafkaRetryUtil.sendWithRetry(ORDER_CREATED_TOPIC, event, 3);
                    }
                });
    }

    // Kafka에 거래 체결 이벤트 보내기
    @Override
    public void sendTransactionCreated(TransactionCreatedEvent event) {
        kafkaTemplate.send(TRANSACTION_CREATED_TOPIC, event) // 객체 직접 전송
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Kafka 전송 성공: tradeId={}", event.getTradeId());
                    } else {
                        kafkaRetryUtil.sendWithRetry(TRANSACTION_CREATED_TOPIC, event, 3);
                    }
                });
    }
}
