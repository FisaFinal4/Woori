package com.piehouse.woorepie.global.kafka.controller;

import com.piehouse.woorepie.global.kafka.service.impliment.KafkaProducerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kafka")
public class KafkaController {

    private final KafkaProducerServiceImpl producerService;

    @PostMapping("/test")
    public String sendToA(@RequestParam String message) {
        producerService.sendToTopicTest(message);
        return "sent to topic test";
    }

}
