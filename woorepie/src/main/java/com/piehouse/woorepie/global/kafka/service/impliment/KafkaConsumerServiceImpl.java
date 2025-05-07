package com.piehouse.woorepie.global.kafka.service.impliment;

import com.piehouse.woorepie.global.kafka.service.KafkaConsumerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    @Override
    @KafkaListener(topics = "test", groupId = "group-test")
    public void listenToTopicTest(String message) {
        System.out.println("Received from topic-test: " + message);
    }

}
