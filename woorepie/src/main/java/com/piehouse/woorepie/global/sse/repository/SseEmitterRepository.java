package com.piehouse.woorepie.global.sse.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SseEmitterRepository {

    private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    public void save(Long customerId, SseEmitter emitter) {
        emitterMap.put(customerId, emitter);
    }

    public SseEmitter get(Long customerId) {
        return emitterMap.get(customerId);
    }

    public void remove(Long customerId) {
        emitterMap.remove(customerId);
    }
}
