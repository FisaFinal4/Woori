package com.piehouse.woorepie.notice.service;

import com.piehouse.woorepie.estate.entity.Estate;
import com.piehouse.woorepie.estate.repository.EstateRepository;
import com.piehouse.woorepie.global.exception.CustomException;
import com.piehouse.woorepie.global.exception.ErrorCode;
import com.piehouse.woorepie.notice.dto.request.CreateNoticeRequest;
import com.piehouse.woorepie.notice.dto.response.GetNoticeSimpleResponse;
import com.piehouse.woorepie.notice.entity.Notice;
import com.piehouse.woorepie.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final EstateRepository estateRepository;

    // 공시 등록
    @Transactional
    public void create(CreateNoticeRequest request) {
        Estate estate = estateRepository.findById(request.getEstateId())
                .orElseThrow(() -> new CustomException(ErrorCode.ESTATE_NOT_FOUND));

        Notice notice = Notice.builder()
                .estate(estate)
                .noticeTitle(request.getNoticeTitle())
                .noticeContent(request.getNoticeContent())
                .noticeFileUrl(request.getNoticeFileUrl())
                .noticeDate(LocalDateTime.now())
                .build();

        noticeRepository.save(notice);
    }

    // 공시 리스트 조회
    public List<GetNoticeSimpleResponse> getNoticeList() {
        List<Notice> noticeList = noticeRepository.findAllByOrderByNoticeDateDesc();

        return noticeList.stream()
                .map(notice -> new GetNoticeSimpleResponse(
                        notice.getNoticeId(),
                        notice.getEstate().getEstateId(),
                        notice.getEstate().getEstateName(),
                        notice.getNoticeTitle(),
                        notice.getNoticeDate()
                ))
                .collect(Collectors.toList());
    }


}
