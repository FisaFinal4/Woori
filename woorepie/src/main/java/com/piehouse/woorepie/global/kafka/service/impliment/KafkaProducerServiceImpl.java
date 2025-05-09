package com.piehouse.woorepie.global.kafka.service.impliment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.piehouse.woorepie.global.kafka.request.dto.KafkaProducerDto;
import com.piehouse.woorepie.global.kafka.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    private static final String TEST_TOPIC = "test";
    private static final String ORDER_CREATED_TOPIC = "order.created";

    @Override
    public void sendToTopicTest(String message) {
        kafkaTemplate.send(TEST_TOPIC, message);
    }

    @Override
    public void sendOrder(KafkaProducerDto message) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(ORDER_CREATED_TOPIC, payload);
            log.info("Kafka 전송 성공 - 토픽: {}, 메시지: {}", ORDER_CREATED_TOPIC, payload);
        } catch (JsonProcessingException e) {
            log.error("Kafka 직렬화 실패: {}", e.getMessage());
        }
    }
}
