package com.nexus.backend.api.shortUrl.repository;

import com.nexus.backend.api.shortUrl.entity.QShortUrl;
import com.nexus.backend.api.shortUrl.entity.QShortUrlStats;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;



@Repository
@RequiredArgsConstructor
public class ShortUrlQueryRepository {

    private final JPAQueryFactory queryFactory;

    /**
     * 전체 링크 수 조회
     */
    public Long getTotalLinks(){
        QShortUrl shortUrl = QShortUrl.shortUrl1;

        Long res = queryFactory
                .select(shortUrl.count())
                .from(shortUrl)
                .fetchOne();
        return res != null ? res : 0L;
    }

    /**
     * 전체 클릭 수 조회
     */
   public Long getTotalClicks() {
       QShortUrlStats shortUrlStats = QShortUrlStats.shortUrlStats;

       Long result = queryFactory
               .select(shortUrlStats.clickCount.sum())
               .from(shortUrlStats)
               .fetchOne();

       return result != null ? result : 0L;
   }

    /**
     * 특정 기간의 총 클릭 수
     */

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