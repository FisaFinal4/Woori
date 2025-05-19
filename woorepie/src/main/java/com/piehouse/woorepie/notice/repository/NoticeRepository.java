package com.piehouse.woorepie.notice.repository;

import com.piehouse.woorepie.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("select n from Notice n join fetch n.estate order by n.noticeDate desc")
    List<Notice> findAllWithEstateOrderByNoticeDateDesc();

}
