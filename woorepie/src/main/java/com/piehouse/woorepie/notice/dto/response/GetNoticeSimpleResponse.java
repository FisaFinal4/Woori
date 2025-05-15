package com.piehouse.woorepie.notice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class GetNoticeSimpleResponse {

    private Long noticeId;

    private Long estateId;

    private String estateName;

    private String noticeTitle;

    private LocalDateTime noticeDate;

}
