package com.piehouse.woorepie.notice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ModifyNoticeRequest {

    @NotBlank(message = "제목은 비어 있을 수 없습니다.")
    private String noticeTitle;

    @NotBlank(message = "내용은 비어 있을 수 없습니다.")
    private String noticeContent;

    private String noticeFileUrl;

}
