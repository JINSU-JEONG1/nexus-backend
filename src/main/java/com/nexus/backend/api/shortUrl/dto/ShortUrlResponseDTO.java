package com.nexus.backend.api.shortUrl.dto;

import com.nexus.backend.api.shortUrl.entity.ShortUrl;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShortUrlResponseDTO {

    private String originUrl;
    private String shortUrl;

    /**
     * Entity를 DTO로 변환하는 정적 팩토리 메서드
     */
    public static ShortUrlResponseDTO from(ShortUrl entity, String baseUrl) {
        return ShortUrlResponseDTO.builder()
                .originUrl(entity.getOriginUrl())
                .shortUrl(baseUrl + entity.getShortUrl())
                .build();
    }
}
