package com.piehouse.woorepie.notice.controller;

import com.piehouse.woorepie.agent.dto.SessionAgent;
import com.piehouse.woorepie.global.response.ApiResponse;
import com.piehouse.woorepie.global.response.ApiResponseUtil;
import com.piehouse.woorepie.notice.dto.request.CreateNoticeRequest;
import com.piehouse.woorepie.notice.dto.request.ModifyNoticeRequest;
import com.piehouse.woorepie.notice.dto.response.GetNoticeDetailsResponse;
import com.piehouse.woorepie.notice.dto.response.GetNoticeSimpleResponse;
import com.piehouse.woorepie.notice.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    // 공시 등록 (중개인 로그인 필요)
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createNotice(@Valid @RequestBody CreateNoticeRequest request,
                                                            @AuthenticationPrincipal SessionAgent sessionAgent,
                                                            HttpServletRequest httpRequest) {
        noticeService.create(request, sessionAgent.getAgentId());
        return ApiResponseUtil.of(HttpStatus.CREATED, "공시 등록 성공", null, httpRequest);
    }

    // 공시 리스트 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<GetNoticeSimpleResponse>>> getNotices(HttpServletRequest request) {
        List<GetNoticeSimpleResponse> notices = noticeService.getNoticeList();
        return ApiResponseUtil.success(notices, request);
    }

    // 공시 상세 조회 (파라미터 방식)
    @GetMapping(params = "noticeId")
    public ResponseEntity<ApiResponse<GetNoticeDetailsResponse>> getNoticeDetails(
            @RequestParam("noticeId") Long noticeId,
            HttpServletRequest request
    ) {
        GetNoticeDetailsResponse dto = noticeService.getNoticeDetails(noticeId);
        return ApiResponseUtil.success(dto, request);
    }

    // 공시 수정 (중개인 로그인 필요)
    @PatchMapping("/modify")
    public ResponseEntity<ApiResponse<String>> modifyNotice(
            @RequestParam("noticeId") Long noticeId,
            @Valid @RequestBody ModifyNoticeRequest request,
            @AuthenticationPrincipal SessionAgent sessionAgent,
            HttpServletRequest httpRequest
    ) {
        noticeService.modifyNotice(noticeId, request, sessionAgent.getAgentId());
        return ApiResponseUtil.success("공시 수정 성공", httpRequest);
    }
}
