package com.nexus.backend.api.shortUrl.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShortUrlStatsResponseDTO {

    private Kpi kpi;
    private Trend trends;

    @Getter
    @Builder
    public static class Kpi {
        private long totalLinks;            // 전체 생성된 링크 수
        private long totalClicks;           // 전체 클릭 수
        private long currentClicks;          // 기간별 클릭 수
        private long prevClicks;            // 저번 기간 클릭수
        private long periodClicksChange;    // 기간별 클릭 수 변화량
        private double avgClickRateChange;  // 평균 클릭률 변화량
        private long todayClicked;          // 오늘 클릭 수

        @QueryProjection
        public Kpi(long totalLinks, long totalClicks, long currentClicks,
                   long prevClicks, double periodClicksChange,
                   double avgClickRateChange, long todayClicked) {
            this.totalLinks = totalLinks;
            this.totalClicks = totalClicks;
            this.currentClicks = currentClicks;
            this.prevClicks = prevClicks;
            this.avgClickRateChange = avgClickRateChange;
            this.todayClicked = todayClicked;
        }
    }


    @Getter
    @Builder
    public static class Trend {
        private List<String> labels;
        private List<Long> created;
        private List<Long> clicks;

        @QueryProjection    public Trend(List<String> labels, List<Long> created, List<Long> clicks) {
            this.labels = labels;
            this.created = created;
            this.clicks = clicks;
        }
    }
}