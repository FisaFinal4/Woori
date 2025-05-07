package com.piehouse.woorepie.global.kafka.service.impliment;

import com.piehouse.woorepie.global.kafka.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendToTopicTest(String message) {
        kafkaTemplate.send("test", message);
    }

}