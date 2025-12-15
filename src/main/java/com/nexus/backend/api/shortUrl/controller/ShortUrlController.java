package com.nexus.backend.api.shortUrl.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

/**
 * short url 관리 컨트롤러
 */
@Tag(name = "short url 관리 컨트롤러", description = "short url API")
@Slf4j
@RestController  
@RequestMapping("/api/short-url")
@RequiredArgsConstructor
public class ShortUrlController {
    
    // TODO: ShortUrlService 주입 필요
    // private final ShortUrlService shortUrlService;
    
    /**
     * 짧은 URL 생성
     */
//    @Operation(summary = "짧은 URL 생성", description = "원본 URL을 입력받아 짧은 URL을 생성합니다")
//    @PostMapping
//    public ResponseEntity<ShortUrlResponse> createShortUrl(@Valid
//        @RequestBody ShortUrlCreateRequest request) {
//        log.info("짧은 URL 생성 요청: {}", request.getLongUrl());
//
//        // TODO: 서비스 구현 필요
//        // ShortUrlResponse response = shortUrlService.createShortUrl(request.getLongUrl());
//
//        // 임시 응답 (서비스 구현 전까지)
//        ShortUrlResponse response = ShortUrlResponse.builder()
//                .shortCode("temp123")
//                .shortUrl("http://localhost:4000/api/short-url/temp123")
//                .longUrl(request.getLongUrl())
//                .build();
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
    
    /**
     * 짧은 URL 정보 조회
     */
//    @Operation(summary = "짧은 URL 정보 조회", description = "짧은 URL 코드로 원본 URL 정보를 조회합니다")
//    @GetMapping("/{shortCode}")
//    public ResponseEntity<ShortUrlResponse> getShortUrl(@PathVariable String shortCode) {
//        log.info("짧은 URL 조회 요청: {}", shortCode);
//
//        // TODO: 서비스 구현 필요
//        // ShortUrlResponse response = shortUrlService.getShortUrl(shortCode);
//
//        // 임시 응답 (서비스 구현 전까지)
//        ShortUrlResponse response = ShortUrlResponse.builder()
//                .shortCode(shortCode)
//                .shortUrl("http://localhost:4000/api/short-url/" + shortCode)
//                .longUrl("https://example.com")
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
    
    /**
     * 짧은 URL로 리다이렉트
     */
//    @Operation(summary = "짧은 URL 리다이렉트", description = "짧은 URL 코드로 원본 URL로 리다이렉트합니다")
//    @GetMapping("/{shortCode}/redirect")
//    public RedirectView redirectToLongUrl(@PathVariable String shortCode) {
//        log.info("리다이렉트 요청: {}", shortCode);
//
//        // TODO: 서비스 구현 필요
//        // String longUrl = shortUrlService.getLongUrl(shortCode);
//        // return new RedirectView(longUrl);
//
//        // 임시 리다이렉트 (서비스 구현 전까지)
//        return new RedirectView("https://example.com");
//    }
}
