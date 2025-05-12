package com.piehouse.woorepie.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class GetNoticeDetailsResponse {
    private Long noticeId;
    private Long estateId;
    private String estateName;
    private String noticeTitle;
    private String noticeContent;
    private String noticeFileUrl;
    private LocalDateTime noticeDate;
}

