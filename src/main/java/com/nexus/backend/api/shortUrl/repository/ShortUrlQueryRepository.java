package com.nexus.backend.api.shortUrl.repository;

import com.nexus.backend.api.shortUrl.dto.QShortUrlStatsResponseDTO_Kpi;
import com.nexus.backend.api.shortUrl.dto.ShortUrlStatsRequestDTO;
import com.nexus.backend.api.shortUrl.dto.ShortUrlStatsResponseDTO;
import com.nexus.backend.api.shortUrl.entity.QShortUrl;
import com.nexus.backend.api.shortUrl.entity.QShortUrlStats;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import org.springframework.expression.spel.ast.Projection;
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

                // periodClicks (기간별 클릭 수)
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
     * JOIN 사용하여 한 번의 쿼리로 처리
     */

    /**
     * 최다 클릭 URL Top N
     */

    /**
     * 특정 기간 최다 클릭 URL Top N
     */

}