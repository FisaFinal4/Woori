package com.piehouse.woorepie.notification.service.implement;

import com.piehouse.woorepie.customer.entity.Customer;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.sse.repository.SseEmitterRepository;
import com.piehouse.woorepie.global.util.NotificationContentUtils;
import com.piehouse.woorepie.notification.dto.response.NotificationResponse;
import com.piehouse.woorepie.notification.entity.Notification;
import com.piehouse.woorepie.notification.repository.NotificationRepository;
import com.piehouse.woorepie.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;

import static com.piehouse.woorepie.global.exception.ErrorCode.NOTIFICATION_NON_EXIST;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final SseEmitterRepository emitterRepository;

    // 읽지 않은 알림 조회
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(Long customerId) {
        return notificationRepository.findByCustomer_CustomerIdAndIsReadFalse(customerId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }


    // 전체 알림 조회 (읽은/안읽은)
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications(Long customerId) {
        return notificationRepository.findByCustomer_CustomerId(customerId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    // 알림 읽음 처리
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(NOTIFICATION_NON_EXIST));
        notification.markAsRead();
    }

    @Transactional
    // 거래 체결 시 알림 생성 및 전송
    public void sendTradeNotification(
            Customer customer,
            String assetName,
            int price,
            int tokenAmount,
            LocalDateTime tradeTime,
            boolean isBuy
    ) {
        log.info("[알림 생성 시도] 고객 ID: {}, 매물명: {}", customer.getCustomerId(), assetName);

        // 1. 알림 내용 생성
        String content = NotificationContentUtils.createNotification(
                customer.getCustomerName(),
                assetName,
                price,
                tokenAmount,
                tradeTime,
                isBuy
        );

        log.debug("생성된 알림 내용: {}", content);

        // 2. DB 저장
        Notification notification = notificationRepository.save(
                Notification.builder()
                        .customer(customer)
                        .content(content)
                        .isRead(false)
                        .build()
        );

        log.info("[알림 저장 완료] 알림 ID: {}", notification.getNotificationId());

        // 3. SSE 알림 전송
        SseEmitter emitter = emitterRepository.get(customer.getCustomerId());
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("trade-notification")
                        .data(NotificationResponse.from(notification)));
            } catch (Exception e) {
                emitterRepository.remove(customer.getCustomerId());
            }
        }
    }
}
