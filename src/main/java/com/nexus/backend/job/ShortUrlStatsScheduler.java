package com.nexus.backend.job;

import com.nexus.backend.api.shortUrl.entity.ShortUrl;
import com.nexus.backend.api.shortUrl.entity.ShortUrlStats;
import com.nexus.backend.api.shortUrl.repository.ShortUrlRepository;
import com.nexus.backend.api.shortUrl.repository.ShortUrlStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShortUrlStatsScheduler {

    private final StringRedisTemplate redisTemplate;
    private final ShortUrlRepository shortUrlRepository;
    private final ShortUrlStatsRepository shortUrlStatsRepository;

    private static final String DIRTY_KEY_SET = "dirty_short_urls";
    private static final String CLICK_KEY_PREFIX = "click:cnt:";

    @Scheduled(cron = "0 0/5 * * * ?") // 5분마다 실행
    @Transactional
    public void syncClickCountsToDb() {
        Set<String> dirtyShortUrls = redisTemplate.opsForSet().members(DIRTY_KEY_SET);
        if (dirtyShortUrls == null || dirtyShortUrls.isEmpty()) {
            return;
        }

        log.info("Stats Sync Started. Targets: {}", dirtyShortUrls.size());

        LocalDate today = LocalDate.now(); // 통계 기준 날짜 고정
        Map<String, Long> clickCounts = new HashMap<>();
        List<String> processedKeys = new ArrayList<>();

        // Redis에서 카운트 가져오기 (Atomic GetAndSet to 0)
        for (String shortKey : dirtyShortUrls) {
            String val = redisTemplate.opsForValue().getAndSet(CLICK_KEY_PREFIX + shortKey, "0");
            long clicks = (val == null) ? 0L : Long.parseLong(val);
            
            if (clicks > 0) {
                clickCounts.put(shortKey, clicks);
            }
            processedKeys.add(shortKey);
        }

        if (clickCounts.isEmpty()) {
            if (!processedKeys.isEmpty()) {
                redisTemplate.opsForSet().remove(DIRTY_KEY_SET, processedKeys.toArray());
            }
            return;
        }

        // ShortURL Entity 조회 (ID 매핑용)
        List<ShortUrl> shortUrlList = shortUrlRepository.findByShortUrlIn(clickCounts.keySet());
        Map<String, ShortUrl> shortUrlMap = shortUrlList.stream()
                .collect(Collectors.toMap(ShortUrl::getShortUrl, Function.identity()));

        List<Long> shortUrlIds = shortUrlList.stream().map(ShortUrl::getId).toList();

        // ShortUrlStats 조회 (ID 사용)
        // List<ShortUrlStats> statsList = shortUrlStatsRepository.findByShortUrlIdIn(shortUrlIds);
        List<ShortUrlStats> statsList = shortUrlStatsRepository.findByShortUrlIdInAndStatDate(shortUrlIds, today);
        Map<Long, ShortUrlStats> statsMap = statsList.stream()
                .collect(Collectors.toMap(ShortUrlStats::getShortUrlId, Function.identity()));

        List<ShortUrlStats> toSave = new ArrayList<>();

        // 병합 및 업데이트
        for (Map.Entry<String, Long> entry : clickCounts.entrySet()) {
            ShortUrl shortUrl = shortUrlMap.get(entry.getKey());
            if (shortUrl == null) continue;
            
            // 오늘 자 데이터가 있으면 가져오고, 없으면 오늘 날짜로 새로 생성
            ShortUrlStats stats = statsMap.get(shortUrl.getId());
            if (stats == null) {
                stats = ShortUrlStats.builder()
                        .shortUrlId(shortUrl.getId())
                        .statDate(today) // 날짜 명시적 주입
                        .build();
            }
            
            stats.addClicks(entry.getValue());
            toSave.add(stats);
        }

        // 저장
        shortUrlStatsRepository.saveAll(toSave);

        // Redis Dirty Set 정리
        if (!processedKeys.isEmpty()) {
            redisTemplate.opsForSet().remove(DIRTY_KEY_SET, processedKeys.toArray());
        }
        
        log.info("Stats Sync Completed. Updated {} records.", toSave.size());
    }
}
