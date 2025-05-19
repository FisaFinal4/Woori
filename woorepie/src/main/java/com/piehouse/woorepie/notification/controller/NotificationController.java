package com.piehouse.woorepie.notification.controller;

import com.piehouse.woorepie.customer.dto.SessionCustomer;
import com.piehouse.woorepie.global.sse.repository.SseEmitterRepository;
import com.piehouse.woorepie.notification.dto.response.NotificationResponse;
import com.piehouse.woorepie.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SseEmitterRepository emitterRepository;

    /**
     * 클라이언트에서 SSE 구독
     */
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal SessionCustomer sessionCustomer) {
        Long customerId = sessionCustomer.getCustomerId();
        log.info("✅ SSE 구독 시작 - 고객 ID: {}", customerId);


        // 기본 타임아웃: 60초 (필요 시 더 길게 설정 가능)
        SseEmitter emitter = new SseEmitter(60 * 1000L);

        // 저장소에 등록
        emitterRepository.save(customerId, emitter);

        // 에러/타임아웃/완료 시 제거
        emitter.onCompletion(() -> emitterRepository.remove(customerId));
        emitter.onTimeout(() -> emitterRepository.remove(customerId));
        emitter.onError((e) -> emitterRepository.remove(customerId));

        try {
            emitter.send(SseEmitter.event().name("connect").data("SSE connected"));
            emitter.send(SseEmitter.event().name("trade-notification").data("\"테스트 알림\""));
            log.info("[SSE 연결 응답 전송 완료] 고객 ID: {}", customerId);
        } catch (Exception e) {
            log.error("[SSE 응답 중 예외 발생]", e);
            emitterRepository.remove(customerId);
        }

        return emitter;
    }

    /**
     * 읽지 않은 알림 목록 조회
     */
    @GetMapping("/unread")
    public List<NotificationResponse> getUnreadNotifications(@AuthenticationPrincipal SessionCustomer sessionCustomer) {
        return notificationService.getUnreadNotifications(sessionCustomer.getCustomerId());
    }

    /**
     * 전체 알림 조회 (읽은 + 안읽은)
     */
    @GetMapping
    public List<NotificationResponse> getAllNotifications(@AuthenticationPrincipal SessionCustomer sessionCustomer) {
        return notificationService.getAllNotifications(sessionCustomer.getCustomerId());
    }

    /**
     * 알림 읽음 처리
     */
    @PostMapping("/{notificationId}/read")
    public void markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
    }
}
