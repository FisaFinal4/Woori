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
     * key x - 순서 제어 필요 x
     * @param topic         전송할 토픽명
     * @param message       전송할 메시지(이벤트 객체)
     * @param maxRetries    최대 재시도 횟수
     */
    public <T> void sendWithRetry(String topic, T message, int maxRetries) {
        sendWithRetryInternal(topic, null, message, maxRetries, 0);
    }

    /**
     * Kafka 메시지를 비동기로 전송하며, 실패 시 최대 maxRetries까지 재시도한다.
     * key - 파티셔닝 제어, 순서 보장
     * @param key         전송할 토픽명
     */
    public <T> void sendWithRetry(String topic, String key, T message, int maxRetries) {
        sendWithRetryInternal(topic, key, message, maxRetries, 0);
    }

    private <T> void sendWithRetryInternal(String topic, String key, T message, int maxRetries, int currentRetry) {
        CompletableFuture<?> future = (key == null) ?
                kafkaTemplate.send(topic, message) :
                kafkaTemplate.send(topic, key, message);

        future.whenCompleteAsync((result, ex) -> {
            if (ex == null) {
                log.info("Kafka 전송 성공 | topic={} | message={}", topic, message);
            } else {
                if (currentRetry < maxRetries) {
                    int delaySeconds = (int) Math.pow(2, currentRetry); // Exponential Backoff
                    log.warn("Kafka 전송 실패, {}초 후 재시도 {}/{} | topic={} | error={}",
                            delaySeconds, currentRetry + 1, maxRetries, topic, ex.getMessage());

                    CompletableFuture.delayedExecutor(delaySeconds, TimeUnit.SECONDS)
                            .execute(() -> sendWithRetryInternal(topic, key, message, maxRetries, currentRetry + 1));
                } else {
                    log.error("Kafka 전송 최종 실패 | topic={} | message={}", topic, message, ex);
                    // (선택) DLQ 전송 등 추가 처리 가능
                }
            }
        });
    }

}
