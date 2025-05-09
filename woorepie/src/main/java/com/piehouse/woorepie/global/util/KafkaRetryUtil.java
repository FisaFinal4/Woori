package com.piehouse.woorepie.global.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaRetryUtil {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Kafka 메시지를 비동기로 전송하며, 실패 시 최대 maxRetries까지 재시도한다.
     * @param topic         전송할 토픽명
     * @param message       전송할 메시지(이벤트 객체)
     * @param maxRetries    최대 재시도 횟수
     */
    public <T> void sendWithRetry(String topic, T message, int maxRetries) {
        sendWithRetryInternal(topic, message, maxRetries, 0);
    }

    private <T> void sendWithRetryInternal(String topic, T message, int maxRetries, int currentRetry) {
        kafkaTemplate.send(topic, message)
                .whenCompleteAsync((result, ex) -> {
                    if (ex == null) {
                        log.info("✅ Kafka 전송 성공 | topic={} | message={}", topic, message);
                    } else {
                        if (currentRetry < maxRetries) {
                            int delaySeconds = (int) Math.pow(2, currentRetry); // Exponential Backoff
                            log.warn("⚠️ Kafka 전송 실패, {}초 후 재시도 {}/{} | topic={} | error={}",
                                    delaySeconds, currentRetry + 1, maxRetries, topic, ex.getMessage());

                            CompletableFuture.delayedExecutor(delaySeconds, TimeUnit.SECONDS)
                                    .execute(() -> sendWithRetryInternal(topic, message, maxRetries, currentRetry + 1));
                        } else {
                            log.error("❌ Kafka 전송 최종 실패 | topic={} | message={}", topic, message, ex);
                            // (선택) DLQ 전송 등 추가 처리 가능
                        }
                    }
                });
    }
}
