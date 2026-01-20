package com.nexus.backend.api.shortUrl.dto;

import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ShortUrlStatsResponseDTO {

    private Summary summary;
    private List<TrendItem> trends;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class Summary {
        private long totalLinks;
        private long totalClicks;
        private double avgClickRate;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TrendItem {
        private String label;      // "2026-01-20"
        private long linkCount;    // 해당 기간 생성된 링크 수
        private long clickCount;   // 해당 기간 총 클릭수
    }
}