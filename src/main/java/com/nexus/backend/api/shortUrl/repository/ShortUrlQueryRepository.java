package com.nexus.backend.api.shortUrl.repository;

import com.nexus.backend.api.shortUrl.dto.QShortUrlStatsResponseDTO_Kpi;
import com.nexus.backend.api.shortUrl.dto.ShortUrlStatsResponseDTO;
import com.nexus.backend.api.shortUrl.entity.QShortUrl;
import com.nexus.backend.api.shortUrl.entity.QShortUrlStats;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.stereotype.Repository;



@Repository
@RequiredArgsConstructor
public class ShortUrlQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 전체 링크 수 조회
     */
    public Long getTotalLinks(){
        QShortUrl qUrl = QShortUrl.shortUrl1;

        Long res = queryFactory
                .select(qUrl.count())
                .from(qUrl)
                .fetchOne();
        return res != null ? res : 0L;
    }

    /**
     * 전체 클릭 수 조회
     */
   public Long getTotalClicks() {
       QShortUrlStats qStats = QShortUrlStats.shortUrlStats;

       Long result = queryFactory
               .select(qStats.clickCount.sum())
               .from(qStats)
               .fetchOne();

       return result != null ? result : 0L;
   }

    /**
     * 특정 기간의 총 클릭 수
     */
    public ShortUrlStatsResponseDTO.Kpi getKpi(LocalDate prevStart, LocalDate currentStart) {
        QShortUrlStats qStats = QShortUrlStats.shortUrlStats;
        QShortUrl qUrl = QShortUrl.shortUrl1;

        return queryFactory
            .select(new QShortUrlStatsResponseDTO_Kpi(
                // totalLinks (전체 링크수)
                qUrl.id.countDistinct().coalesce(0L),

                // totalClicks (전체 클릭수)
                qStats.clickCount.sum().longValue().coalesce(0L),

                // currentClicks (기간별 클릭 수)
                new CaseBuilder()
                        .when(qStats.statDate.goe(currentStart))
                        .then(qStats.clickCount)
                        .otherwise(0L)
                        .sum().longValue().coalesce(0L),

                // prevClicks (저번 기간 클릭수)
                new CaseBuilder()
                        .when(qStats.statDate.goe(prevStart).and(qStats.statDate.lt(currentStart)))
                        .then(qStats.clickCount)
                        .otherwise(0L)
                        .sum().longValue().coalesce(0L),

                // periodClicksChange (변화량 - 일단 0.0)
                Expressions.constant(0.0),

                // avgClickRateChange (변화량 - 일단 0.0)
                Expressions.constant(0.0),

                // todayClicked (오늘 클릭 수 - 이전 기간 쿼리 logic 활용)
                new CaseBuilder()
                        .when(qStats.statDate.goe(LocalDate.now()))
                        .then(qStats.clickCount)
                        .otherwise(0L)
                        .sum().longValue().coalesce(0L)
            ))
            .from(qUrl)
            .leftJoin(qStats).on(qUrl.id.eq(qStats.shortUrlId))
            .fetchOne();

    }

    /**
     * 통합 트렌드 조회 (클릭 + 생성 수)
     * Rolling Window 방식으로 period별 집계
     * @param startDate 조회 시작일
     * @param endDate 조회 종료일 (오늘)
     * @return period별 날짜, 생성수, 클릭수 리스트
     */
    public ShortUrlStatsResponseDTO.Trend getTrend(LocalDate startDate, LocalDate endDate) {
        QShortUrl qUrl = QShortUrl.shortUrl1;
        QShortUrlStats qStats = QShortUrlStats.shortUrlStats;

        // 생성된링크 수 조회 (createdAt 기준으로 날짜별 집계)
        var createdData = queryFactory
                .select(
                    Expressions.stringTemplate("DATE({0})", qUrl.createdAt),
                    qUrl.count()
                )
                .from(qUrl)
                .where(qUrl.createdAt.between(
                        startDate.atStartOfDay(), 
                        endDate.atTime(23, 59, 59)))
                .groupBy(Expressions.stringTemplate("DATE({0})", qUrl.createdAt))
                .orderBy(Expressions.stringTemplate("DATE({0})", qUrl.createdAt).asc())
                .fetch();

        // 클릭 수 조회 (statDate 기준으로 날짜별 집계)
        var clickData = queryFactory
                .select(
                    qStats.statDate,
                    qStats.clickCount.sum()
                )
                .from(qStats)
                .where(qStats.statDate.between(startDate, endDate))
                .groupBy(qStats.statDate)
                .orderBy(qStats.statDate.asc())
                .fetch();

        // Map으로 변환 (날짜별 데이터 매핑 용이하게)
        java.util.Map<LocalDate, Long> createdMap = new java.util.HashMap<>();
        for (com.querydsl.core.Tuple tuple : createdData) {
            // DATE() 함수는 java.sql.Date를 반환하므로 변환 필요
            java.sql.Date sqlDate = tuple.get(0, java.sql.Date.class);
            LocalDate date = sqlDate.toLocalDate();
            createdMap.put(date, tuple.get(1, Long.class));
        }

        java.util.Map<LocalDate, Long> clickMap = new java.util.HashMap<>();
        for (com.querydsl.core.Tuple tuple : clickData) {
            LocalDate date = tuple.get(0, LocalDate.class);
            clickMap.put(date, tuple.get(1, Long.class));
        }

        // Trend 객체에 Map을 담아 반환 (Service에서 사용)
        return new TrendDataHolder(createdMap, clickMap);
    }

    // Helper class to hold trend data maps
    public static class TrendDataHolder extends ShortUrlStatsResponseDTO.Trend {
        public final java.util.Map<LocalDate, Long> createdMap;
        public final java.util.Map<LocalDate, Long> clickMap;

        public TrendDataHolder(java.util.Map<LocalDate, Long> createdMap, java.util.Map<LocalDate, Long> clickMap) {
            super(null, null, null);
            this.createdMap = createdMap;
            this.clickMap = clickMap;
        }
    }

    /**
     * 최다 클릭 URL Top N
     */

    /**
     * 특정 기간 최다 클릭 URL Top N
     */

}