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
@Table(name = "SHORT_URL" , schema = "nexus")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortUrl extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_url", nullable = false, columnDefinition = "TEXT")
    private String originUrl;

    @Column(name = "short_url", nullable = false, length = 10)
    private String shortUrl;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Builder
    public ShortUrl(String originUrl, String shortUrl, LocalDateTime expiredAt) {
        this.originUrl = originUrl;
        this.shortUrl = shortUrl;
        this.expiredAt = expiredAt;
    }

    public void updateShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }


}
