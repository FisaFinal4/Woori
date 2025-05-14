package com.piehouse.woorepie.global.kafka.service;

import com.piehouse.woorepie.global.kafka.dto.OrderCreatedEvent;
import com.piehouse.woorepie.global.kafka.dto.SubscriptionRequestEvent;

public interface KafkaConsumerService {
    void listenToTopicTest(String message);
    void consumeOrderCreated(OrderCreatedEvent event);
    void consumeSubscriptionRequest(SubscriptionRequestEvent event);
}
