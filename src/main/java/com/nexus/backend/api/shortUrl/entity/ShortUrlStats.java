package com.nexus.backend.api.shortUrl.entity;

import java.time.LocalDateTime;

import com.nexus.backend.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "click_count", nullable = false)
    private Long clickCount = 0L;

    @Column(name = "last_clicked_at")
    private LocalDateTime lastClickedAt;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "referer", columnDefinition = "TEXT")
    private String referer;

    @Builder
    public ShortUrlStats(Long shortUrlId) {
        this.shortUrlId = shortUrlId;
        this.clickCount = 0L;
    }

    public void increment(String userAgent, String referer) {
        this.clickCount++;
        this.lastClickedAt = LocalDateTime.now();
        this.userAgent = userAgent;
        this.referer = referer;
    }


}
