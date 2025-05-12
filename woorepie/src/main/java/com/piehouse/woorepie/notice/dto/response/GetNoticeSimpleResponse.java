package com.piehouse.woorepie.notice.dto.response;

import java.time.LocalDateTime;

public record GetNoticeSimpleResponse(
        Long noticeId,
        Long estateId,
        String estateName,
        String noticeTitle,
        LocalDateTime noticeDate
) {
}
