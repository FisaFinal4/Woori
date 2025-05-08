package com.piehouse.woorepie.global.kafka.service;

import com.piehouse.woorepie.global.kafka.dto.TransactionCreatedEvent;

public interface KafkaConsumerService {

    void listenToTopicTest(String message);
    void consumeTransactionCreated(TransactionCreatedEvent event);
}
