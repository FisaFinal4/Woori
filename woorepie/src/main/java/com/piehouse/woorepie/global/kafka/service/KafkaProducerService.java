package com.piehouse.woorepie.global.kafka.service;

import com.piehouse.woorepie.global.kafka.request.dto.KafkaProducerDto;

public interface KafkaProducerService {
    void sendToTopicTest(String message);  // 테스트용
    void sendOrder(KafkaProducerDto message); // 실제 전송
}

