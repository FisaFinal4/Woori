package com.piehouse.woorepie.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationContentUtils {
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초");

    public static String createNotification(
            String customerName,
            String assetName,
            long price,
            int quantity,
            LocalDateTime tradeTime,
            boolean isBuy // 매수: true, 매도: false
    ) {
        String tradeType = isBuy ? "매수" : "매도";
        String formattedPrice = String.format("%,d원", price); // 20,000원 형식
        String formattedTime = tradeTime.format(DATE_FORMATTER);

        return String.format(
                "[Woorepie] 거래 체결 안내\n\n" +
                        "%s 고객님, 아래 매물에 대한 %s 주문이 체결되었습니다.\n\n" +
                        "- 매물명: %s\n" +
                        "- 체결 금액: %s\n" +
                        "- 체결 수량: %d 토큰\n" +
                        "- 체결 일시: %s\n\n" +
                        "감사합니다.",
                customerName, tradeType, assetName, formattedPrice, quantity, formattedTime
        );
    }
}
