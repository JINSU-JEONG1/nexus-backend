package com.nexus.backend.api.shortUrl.service;

import com.nexus.backend.api.shortUrl.dto.ShortUrlRequestDTO;
import com.nexus.backend.api.shortUrl.dto.ShortUrlResponseDTO;
import com.nexus.backend.api.shortUrl.entity.ShortUrl;
import com.nexus.backend.api.shortUrl.repository.ShortUrlRepository;
import com.nexus.backend.common.utils.Base62Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.short-url.base-url}")
    private String baseUrl; // yml에서 바로 주입

    @Transactional
    public ShortUrlResponseDTO createShortUrl(ShortUrlRequestDTO req) {
        String originUrl = req.getOriginUrl();

        // 기존 URL 있으면 바로 반환
        Optional<ShortUrl> existing = shortUrlRepository.findByOriginUrl(originUrl);
        if (existing.isPresent()) {
            return ShortUrlResponseDTO.from(existing.get(), baseUrl);
        }

        // 새로운 URL 생성 및 저장
        ShortUrl newEntity = shortUrlRepository.save(
                ShortUrl.builder()
                        .originUrl(originUrl)
                        .build());

        // shortKey 생성 및 업데이트
        String shortKey = Base62Utils.encode(newEntity.getId());
        newEntity.updateShortUrl(shortKey);

        return ShortUrlResponseDTO.from(newEntity, baseUrl);
    }

    @Transactional(readOnly = true)
    public String getOriginUrlByShortUrl(String shortUrl) {
        // shortHash 로 originUrl 찾기
        Optional<ShortUrl> entity = shortUrlRepository.findByShortUrl(shortUrl);
        return entity.get().getOriginUrl();
    }
}
