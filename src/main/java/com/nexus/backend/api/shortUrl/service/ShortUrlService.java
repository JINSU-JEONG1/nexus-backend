package com.nexus.backend.api.shortUrl.service;

import com.nexus.backend.common.api.ApiRequest;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.nexus.backend.api.shortUrl.dto.ShortUrlRequestDTO;
import com.nexus.backend.api.shortUrl.dto.ShortUrlResponseDTO;
import com.nexus.backend.api.shortUrl.dto.ShortUrlStatsRequestDTO;
import com.nexus.backend.api.shortUrl.dto.ShortUrlStatsResponseDTO;
import com.nexus.backend.api.shortUrl.entity.ShortUrl;
import com.nexus.backend.api.shortUrl.repository.ShortUrlQueryRepository;
import com.nexus.backend.api.shortUrl.repository.ShortUrlRepository;
import com.nexus.backend.common.utils.Base62Utils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * shortUrl service
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ShortUrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final ShortUrlQueryRepository shortUrlQueryRepository;
    private final CacheManager cacheManager;
    private final StringRedisTemplate redisTemplate;

    @Value("${app.short-url.base-url}")
    private String baseUrl; // yml에서 바로 주입

    private static final String CACHE_SHORT = "shortUrls";
    private static final String CACHE_ORIGIN = "originUrls";

    /**
     * Short URL 생성
     * - originUrl 중복 확인 후 생성
     * - 생성된 결과를 shortUrl 캐시에 저장 (Write-Through)
     */
    @Transactional
    public ShortUrlResponseDTO createShortUrl(ShortUrlRequestDTO req) {
        String originUrl = req.getOriginUrl();

        // Redis 중복 확인 (originUrl -> shortKey)
        Cache originCache = cacheManager.getCache(CACHE_ORIGIN);
        if (originCache != null && originCache.get(originUrl) != null) {
            String cachedShortKey = originCache.get(originUrl, String.class);
            log.info("중복 생성 방지 (Cache Hit): {}", originUrl);

            ShortUrl tempEntity = ShortUrl.builder()
                    .originUrl(originUrl)
                    .shortUrl(cachedShortKey)
                    .build();

            return ShortUrlResponseDTO.from(tempEntity, baseUrl);
        }

        // 기존 URL 있으면 바로 반환
        Optional<ShortUrl> existing = shortUrlRepository.findByOriginUrl(originUrl);
        if (existing.isPresent()) {
            ShortUrl entity = existing.get();
            // 캐시 추가
            putInCaches(entity.getShortUrl(), entity.getOriginUrl());
            return ShortUrlResponseDTO.from(entity, baseUrl);
        }

        // 새로운 URL 생성 및 저장
        ShortUrl entity = shortUrlRepository.save(
                ShortUrl.builder()
                        .originUrl(originUrl)
                        .build());

        // shortKey 생성 및 업데이트
        String shortKey = Base62Utils.encode(entity.getId());
        entity.updateShortUrl(shortKey);

        // 캐시에 저장
        putInCaches(shortKey, originUrl);

        return ShortUrlResponseDTO.from(entity, baseUrl);
    }

    @Transactional(readOnly = true)
    public String getOriginUrlByShortUrl(String shortUrl) {
        log.info("getOriginUrlByShortUrl: {}", shortUrl);

        // 통계 집계 (Redis INCR)
        // 에러가 나더라도 본 기능(리다이렉트)에는 영향 없도록 try-catch 
        try {
            redisTemplate.opsForValue().increment("click:cnt:" + shortUrl);
            // 클릭 수가 변한 URL 목록
            redisTemplate.opsForSet().add("dirty_short_urls", shortUrl);
        } catch (Exception e) {
            log.error("Redis stats increment failed", e);
        }

        // 캐시 조회 (Manual Caching)
        Cache cache = cacheManager.getCache(CACHE_SHORT);
        if (cache != null) {
            String cachedUrl = cache.get(shortUrl, String.class);
            if (cachedUrl != null) {
                return cachedUrl;
            }
        }

        // DB에서 엔티티 조회
        Optional<ShortUrl> entity = shortUrlRepository.findByShortUrl(shortUrl);

        // 존재하지 않으면
        if (entity.isEmpty()) {
            throw new EntityNotFoundException("해당 단축 URL을 찾을 수 없습니다: " + shortUrl);
        }

        String originUrl = entity.get().getOriginUrl();

        // 캐시 저장
        if (cache != null) {
            cache.put(shortUrl, originUrl);
        }

        return originUrl;
    }

    // 캐시 저장을 위한 헬퍼 메서드
    private void putInCaches(String shortKey, String originUrl) {
        Cache shortCache = cacheManager.getCache(CACHE_SHORT); // 리다이렉트용
        Cache originCache = cacheManager.getCache(CACHE_ORIGIN); // 중복확인용
        if (shortCache != null)
            shortCache.put(shortKey, originUrl);
        if (originCache != null)
            originCache.put(originUrl, shortKey);
    }

    // KPI 데이터 조회
    @Transactional(readOnly = true)
    public ShortUrlStatsResponseDTO.Kpi getKpi(ApiRequest<ShortUrlStatsRequestDTO> req) {
        String period = req.getData().getPeriod();

        LocalDate now = LocalDate.now();
        LocalDate currentStart;
        LocalDate prevStart;

        // 기간 설정
        switch (period) {
            case "day" -> {
                currentStart = now;
                prevStart = now.minusDays(1);
            }
            case "month" -> {
                // 이번달
                currentStart = now.withDayOfMonth(1);
                // 저번달
                prevStart = currentStart.minusMonths(1);
            }
            default -> { // week (이번 주 월요일 기준)
                currentStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                prevStart = currentStart.minusWeeks(1);
            }
        }

        log.info("currentStart : {} ", currentStart);
        log.info("prevStart : {} ", prevStart);

        //  Raw 데이터 조회
        ShortUrlStatsResponseDTO.Kpi rawData  = shortUrlQueryRepository.getKpi(prevStart, currentStart);

        long curr = rawData.getCurrentClicks();
        long prev = rawData.getPrevClicks();
        long totalClicks = rawData.getTotalClicks();
        long totalLinks = rawData.getTotalLinks();

        // 증감률 계산
        double periodClicksChange;
        if(prev == 0){
            // 지난 데이터가 0인데 현재 데이터가 있으면 100% 증가, 둘 다 0이면 0%
            periodClicksChange = (curr > 0) ? 100.0 : 0.0;
        }else{
            periodClicksChange = ((double)(curr - prev) / prev) * 100;
        }

        // 소수점 두 자리 반올림
        periodClicksChange = Math.round(periodClicksChange * 100.0) / 100.0;

        // 평균 클릭률 계산 미구현
        double avgClickRate = 0.0;

        // 4. 최종 DTO 조립 및 반환 (Builder 결과물을 반드시 return)
        ShortUrlStatsResponseDTO.Kpi result = ShortUrlStatsResponseDTO.Kpi.builder()
                .totalLinks(totalLinks)
                .totalClicks(totalClicks)
                .currentClicks(curr)
                .prevClicks(prev)
                .periodClicksChange(periodClicksChange)
                .avgClickRateChange(avgClickRate)
                .todayClicked(rawData.getTodayClicked())
                .build();

        log.info("KPI Calculation Success: {}", result);
        return result;
    }

    // 추이 차트 데이터 조회
    @Transactional(readOnly = true)
    public ShortUrlStatsResponseDTO.Trend getTrend(ApiRequest<ShortUrlStatsRequestDTO> req) {
        String period = req.getData().getPeriod();
        LocalDate now = LocalDate.now();
        
        LocalDate startDate;
        LocalDate endDate = now;
        
        // Rolling Window 범위 설정
        switch (period) {
            case "day" -> {
                // 최근 7일 (어제까지 6일 + 오늘)
                startDate = now.minusDays(6);
            }
            case "week" -> {
                // 최근 6주 (지난 5주 + 이번 주)
                startDate = now.minusWeeks(5).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            }
            case "month" -> {
                // 최근 12개월 (지난 11개월 + 이번 달)
                startDate = now.minusMonths(11).withDayOfMonth(1);
            }
            default -> {
                startDate = now.minusDays(6);
            }
        }
        
        log.info("Trend Period: {}, StartDate: {}, EndDate: {}", period, startDate, endDate);
        
        // Repository에서 데이터 조회
        ShortUrlQueryRepository.TrendDataHolder dataHolder = 
                (ShortUrlQueryRepository.TrendDataHolder) shortUrlQueryRepository.getTrend(startDate, endDate);
        
        // 라벨 생성 및 데이터 매핑
        java.util.List<String> labels = new java.util.ArrayList<>();
        java.util.List<Long> created = new java.util.ArrayList<>();
        java.util.List<Long> clicks = new java.util.ArrayList<>();
        
        // period별 라벨 생성 및 데이터 집계
        switch (period) {
            case "day" -> {
                // 요일 라벨 생성 (월, 화, 수, 목, 금, 토, 일)
                String[] dayLabels = {"일", "월", "화", "수", "목", "금", "토"};
                for (int i = 0; i < 7; i++) {
                    LocalDate date = startDate.plusDays(i);
                    int dayOfWeek = date.getDayOfWeek().getValue() % 7; // 1=월요일 -> 1, 7=일요일 -> 0
                    labels.add(dayLabels[dayOfWeek]);
                    created.add(dataHolder.createdMap.getOrDefault(date, 0L));
                    clicks.add(dataHolder.clickMap.getOrDefault(date, 0L));
                }
            }
            case "week" -> {
                // 주차 라벨 생성 (1주차, 2주차, ...)
                for (int i = 0; i < 6; i++) {
                    labels.add((i + 1) + "주차");
                    LocalDate weekStart = startDate.plusWeeks(i);
                    LocalDate weekEnd = weekStart.plusDays(6);
                    
                    long weekCreated = 0;
                    long weekClicks = 0;
                    
                    // 해당 주의 모든 날짜를 순회하며 집계
                    for (LocalDate d = weekStart; !d.isAfter(weekEnd); d = d.plusDays(1)) {
                        weekCreated += dataHolder.createdMap.getOrDefault(d, 0L);
                        weekClicks += dataHolder.clickMap.getOrDefault(d, 0L);
                    }
                    
                    created.add(weekCreated);
                    clicks.add(weekClicks);
                }
            }
            case "month" -> {
                // 월 라벨 생성 (1월, 2월, ...)
                for (int i = 0; i < 12; i++) {
                    LocalDate monthDate = startDate.plusMonths(i);
                    labels.add(monthDate.getMonthValue() + "월");
                    LocalDate monthStart = monthDate.withDayOfMonth(1);
                    LocalDate monthEnd = monthDate.withDayOfMonth(monthDate.lengthOfMonth());
                    
                    long monthCreated = 0;
                    long monthClicks = 0;
                    
                    // 해당 월의 모든 날짜를 순회하며 집계
                    for (LocalDate d = monthStart; !d.isAfter(monthEnd); d = d.plusDays(1)) {
                        monthCreated += dataHolder.createdMap.getOrDefault(d, 0L);
                        monthClicks += dataHolder.clickMap.getOrDefault(d, 0L);
                    }
                    
                    created.add(monthCreated);
                    clicks.add(monthClicks);
                }
            }
        }
        
        return ShortUrlStatsResponseDTO.Trend.builder()
                .labels(labels)
                .created(created)
                .clicks(clicks)
                .build();
    }




}
