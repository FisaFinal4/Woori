package com.piehouse.woorepie.global.kafka.service.impliment;

import com.piehouse.woorepie.global.kafka.dto.TransactionCreatedEvent;
import com.piehouse.woorepie.global.kafka.service.KafkaConsumerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    @Override
    @KafkaListener(topics = "test", groupId = "group-test")
    public void listenToTopicTest(String message) {
        System.out.println("Received from topic-test: " + message);
    }

    @Override
    @KafkaListener(topics = "transaction.created", groupId = "group-transaction")
    public void consumeTransactionCreated(TransactionCreatedEvent event) {
        try {
            log.info("거래 체결 이벤트 수신: {}", event);
        } catch (Exception e) {
            log.error("거래 체결 이벤트 처리 실패: {}", event, e);
        }
    }
}
