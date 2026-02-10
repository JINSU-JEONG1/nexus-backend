package com.nexus.backend.api.shortUrl.controller;

import com.nexus.backend.api.shortUrl.dto.ShortUrlRequestDTO;
import com.nexus.backend.api.shortUrl.dto.ShortUrlResponseDTO;
import com.nexus.backend.api.shortUrl.service.ShortUrlService;
import com.nexus.backend.common.api.ApiRequest;
import com.nexus.backend.common.api.ApiResponse;
import com.nexus.backend.common.type.ApiResponseError;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

/**
 * short url 관리 컨트롤러
 */
@Tag(name = "short url 관리 컨트롤러", description = "short url API")
@Slf4j
@RestController
@RequestMapping("/re")
@RequiredArgsConstructor
public class RedirectController {

    private final ShortUrlService shortUrlService;

    /**
     * short URL 리다이렉트
     */
    @Operation(summary = "short URL 리다이렉트", description = "short URL을 입력받아 리다이렉트합니다")
    @GetMapping("/{shortHash}")
    public RedirectView redirect(
            @PathVariable("shortHash") String shortHash) {
        log.info("short URL redirect request hash: {}", shortHash);
        String originUrl = shortUrlService.getOriginUrlByShortUrl(shortHash);
        log.info("short URL redirect originUrl: {}", originUrl);

        // 프로토콜이 없으면 브라우저가 현재 도메인의 하위 경로로 인식하므로 프로토콜 강제 추가
        if (!originUrl.startsWith("http://") && !originUrl.startsWith("https://")) {
            log.info("Protocol missing, prepending https:// to originUrl: {}", originUrl);
            originUrl = "https://" + originUrl;
        }
        
        RedirectView redirectView = new RedirectView(originUrl);
        redirectView.setContextRelative(false); // 절대 경로 사용 강제
        redirectView.setExposeModelAttributes(false); // 모델 속성 노출 방지
        return redirectView;
    }

}
