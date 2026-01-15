package com.nexus.backend.api.shortUrl.service;

import com.nexus.backend.api.shortUrl.dto.ShortUrlRequestDTO;
import com.nexus.backend.api.shortUrl.dto.ShortUrlResponseDTO;
import com.nexus.backend.api.shortUrl.entity.ShortUrl;
import com.nexus.backend.api.shortUrl.repository.ShortUrlRepository;
import com.nexus.backend.common.utils.Base62Utils;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * shortUrl service
 */
@Service
@Slf4j
public class ShortUrlService {

    @Autowired
    private ShortUrlRepository shortUrlRepository;

    @Autowired
    private CacheManager cacheManager;

    @Value("${app.short-url.base-url}")
    private String baseUrl; // yml에서 바로 주입

    /**
     * Short URL 생성
     * - originUrl 중복 확인 후 생성
     * - 생성된 결과를 shortUrl 캐시에 저장 (Write-Through)
     */
    @Transactional
    public ShortUrlResponseDTO createShortUrl(ShortUrlRequestDTO req) {
        String originUrl = req.getOriginUrl();

        // 1. Redis 중복 확인 (originUrl -> shortKey)
        Cache originCache = cacheManager.getCache("originUrls");
        if (originCache != null && originCache.get(originUrl) != null) {
            String cachedShortKey = originCache.get(originUrl, String.class);
            log.info("중복 생성 방지 (Cache Hit): {}", originUrl);

            // of 대신, 기존에 쓰시던 from 방식을 유지하기 위해 임시 엔티티 생성
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

    @Cacheable(value = "shortUrls", key = "#shortUrl")
    @Transactional(readOnly = true)
    public String getOriginUrlByShortUrl(String shortUrl) {
        log.info("getOriginUrlByShortUrl: {}", shortUrl);

        // DB에서 엔티티 조회
        Optional<ShortUrl> entity = shortUrlRepository.findByShortUrl(shortUrl);

        // 존재하지 않으면 즉시 예외 발생 (명시적 처리)
        if (entity.isEmpty()) {
            throw new EntityNotFoundException("해당 단축 URL을 찾을 수 없습니다: " + shortUrl);
        }

        // 존재하면 원본 URL 반환
        return entity.get().getOriginUrl();
    }

    // 캐시 저장을 위한 헬퍼 메서드
    private void putInCaches(String shortKey, String originUrl) {
        Cache shortCache = cacheManager.getCache("shortUrls"); // 리다이렉트용
        Cache originCache = cacheManager.getCache("originUrls"); // 중복확인용
        if (shortCache != null)
            shortCache.put(shortKey, originUrl);
        if (originCache != null)
            originCache.put(originUrl, shortKey);
    }

}
