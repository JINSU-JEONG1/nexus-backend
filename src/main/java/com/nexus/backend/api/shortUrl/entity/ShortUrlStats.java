package com.nexus.backend.api.shortUrl.entity;

import com.nexus.backend.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * shortUrl 엔티티
 */
@Entity 
@Table(name = "SHORT_URL_STATS" , schema = "nexus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortUrlStats extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_url_id", nullable = false)
    private Long shortUrlId;

    @Column(name = "stat_date", nullable = false)
    private LocalDate statDate; // 통계 기준 날짜

    @Column(name = "click_count", nullable = false)
    private Long clickCount = 0L;

    @Column(name = "referer", columnDefinition = "TEXT")
    private String referer;

    @Builder
    public ShortUrlStats(Long shortUrlId, LocalDate statDate) {
        this.shortUrlId = shortUrlId;
        this.statDate = statDate != null ? statDate : LocalDate.now();
        this.clickCount = 0L;
    }

    // Redis 데이터를 DB로 동기화(Bulk Update)할 때 사용
    public void addClicks(long count) {
        this.clickCount += count;
    }

}
