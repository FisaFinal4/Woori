package com.piehouse.woorepie.notification.service;

import com.piehouse.woorepie.customer.entity.Customer;
import com.piehouse.woorepie.notification.dto.response.NotificationResponse;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


public interface NotificationService {
    // 읽지 않은 알림 조회
    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(Long customerId);

    // 전체 알림 조회 (읽은/안읽은)
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications(Long customerId);

    // 알림 읽음 처리
    @Transactional
    public void markAsRead(Long notificationId);

    @Transactional
    // 거래 체결 시 알림 생성 및 전송
    public void sendTradeNotification(
            Customer customer,
            String assetName,
            int price,
            int tokenAmount,
            LocalDateTime tradeTime,
            boolean isBuy
    );
}
