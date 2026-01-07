package com.nexus.backend.api.shortUrl.service;

import com.nexus.backend.api.shortUrl.dto.ShortUrlRequestDTO;
import com.nexus.backend.api.shortUrl.dto.ShortUrlResponseDTO;
import com.nexus.backend.api.shortUrl.entity.ShortUrl;
import com.nexus.backend.api.shortUrl.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * shortUrl service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShortUrlService {

    @Autowired
    private ShortUrlRepository shortUrlRepository;

    @Transactional
    public ShortUrlResponseDTO createShortUrl(ShortUrlRequestDTO req) {
        String originUrl = req.getOriginUrl();
        
        // 1. 기존 URL 조회
        Optional<ShortUrl> existing = shortUrlRepository.findByOriginUrl(originUrl);
        
        if (existing.isPresent()) {
            // 2. 존재하면 → 기존 shortUrl 반환
            ShortUrl entity = existing.get();
            
            // 3. 혹시 shortUrl이 null이면 생성 (데이터 정합성 보장)
            if (entity.getShortUrl() == null) {
                String shortKey = encoder.encode(entity.getId());
                entity.updateShortUrl(shortKey);
                entity = shortUrlRepository.save(entity);
            }
            
            return ShortUrlResponseDTO.from(entity);
        }
        
        // 4. 존재하지 않으면 → 새로 생성
        // 먼저 저장해서 ID를 얻고, 그 ID로 shortUrl 생성
        ShortUrl newEntity = ShortUrl.builder()
            .originUrl(originUrl)
            .shortUrl("temp")  // 임시값 (나중에 업데이트)
            .expiredAt(null)
            .build();
        
        newEntity = shortUrlRepository.save(newEntity);  // ID 생성됨
        
        // ID로 shortUrl 생성
        String shortKey = encoder.encode(newEntity.getId());
        newEntity.updateShortUrl(shortKey);
        newEntity = shortUrlRepository.save(newEntity);
        
        return ShortUrlResponseDTO.from(newEntity);
    }
}
