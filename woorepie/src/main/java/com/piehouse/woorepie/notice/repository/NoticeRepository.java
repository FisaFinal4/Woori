package com.piehouse.woorepie.notice.repository;

import com.piehouse.woorepie.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByOrderByNoticeDateDesc();
}
