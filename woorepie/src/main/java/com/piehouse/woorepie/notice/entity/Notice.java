package com.piehouse.woorepie.notice.entity;

import com.piehouse.woorepie.estate.entity.Estate;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notice")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@ToString(exclude = "estate")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Notice {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estate_id", nullable = false)
    private Estate estate;

    @Column(nullable = false, length = 100)
    private String noticeTitle;

    @Column(columnDefinition = "TEXT")
    private String noticeContent;

    @Column(length = 1000)
    private String noticeFileUrl;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime noticeDate;

    // 공시 수정
    public void updateNotice(String title, String content, String fileUrl) {
        this.noticeTitle = title;
        this.noticeContent = content;
        this.noticeFileUrl = fileUrl;
    }

}
