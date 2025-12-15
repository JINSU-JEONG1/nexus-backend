package com.nexus.backend.api.shortUrl.service;

import com.nexus.backend.api.shortUrl.dto.ShortUrlRequestDTO;
import com.nexus.backend.api.shortUrl.dto.ShortUrlResponseDTO;
import com.nexus.backend.api.shortUrl.entity.ShortUrl;
import com.nexus.backend.api.shortUrl.repository.ShortUrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final ShortUrlRepository shortUrlRepository;


//    @Transactional
//    public ShortUrlResponseDTO findShortUrl(ShortUrlRequestDTO req) {
//
//        // requestDTO에서 값 추출
//        String originUrl = req.getOriginUrl();
//
//        // entity 생성 및 DB 저장
//        ShortUrl entity = shortUrlRepository.save(new ShortUrl(originUrl));
//
//        // 3. ID로 shortKey 생성 및 Entity 업데이트
//        String shortKey = encoder.encode(entity.getId());
//        entity.updateShortKey(shortKey);
//        // 4. 저장된 Entity를 Response DTO로 변환하여 리턴
//        return ShortUrlResponseDTO.from(entity); // 💡 DTO 변환 시점
//    }
}
