package com.piehouse.woorepie.notice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateNoticeRequest {

    @NotNull(message = "estateId는 필수입니다.")
    private Long estateId;

    @NotBlank(message = "공시 제목은 필수입니다.")
    private String noticeTitle;

    private String noticeContent;

    private String noticeFileUrl;
}
