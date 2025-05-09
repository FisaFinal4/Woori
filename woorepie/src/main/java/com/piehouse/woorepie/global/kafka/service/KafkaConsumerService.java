package com.piehouse.woorepie.global.kafka.service;

import com.piehouse.woorepie.global.kafka.dto.OrderCreatedEvent;

public interface KafkaConsumerService {
    void listenToTopicTest(String message);
    void consumeOrderCreated(OrderCreatedEvent event);
}
