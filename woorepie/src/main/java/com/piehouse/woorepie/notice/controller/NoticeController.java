package com.piehouse.woorepie.notice.controller;

import com.piehouse.woorepie.global.response.ApiResponse;
import com.piehouse.woorepie.notice.dto.request.CreateNoticeRequest;
import com.piehouse.woorepie.notice.dto.response.GetNoticeSimpleResponse;
import com.piehouse.woorepie.notice.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createNotice(@Valid @RequestBody CreateNoticeRequest request,
                                                            HttpServletRequest httpRequest) {
        noticeService.create(request);
        return ResponseEntity.status(201).body(ApiResponse.of(
                201,
                "공시 등록 성공",
                httpRequest.getRequestURI(),
                null
        ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GetNoticeSimpleResponse>>> getNotices(HttpServletRequest request) {
        List<GetNoticeSimpleResponse> notices = noticeService.getNoticeList();

        return ResponseEntity.ok(ApiResponse.of(
                200,
                "공시 리스트 조회 성공",
                request.getRequestURI(),
                notices
        ));
    }
}


