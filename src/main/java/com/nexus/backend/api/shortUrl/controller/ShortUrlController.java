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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

/**
 * short url 관리 컨트롤러
 */
@Tag(name = "short url 관리 컨트롤러", description = "short url API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class ShortUrlController {

    // TODO: ShortUrlService 주입 필요
    @Autowired
    private final ShortUrlService shortUrlService;

    /**
     * 짧은 URL 생성
     */
    @Operation(summary = "짧은 URL 생성", description = "원본 URL을 입력받아 짧은 URL을 생성합니다")
    @PostMapping(value = "/api/short-url/create.api")
    public ApiResponse<ShortUrlResponseDTO> createShortUrl(
            @RequestBody @Valid ApiRequest<ShortUrlRequestDTO> request,
            Errors errors) {

        if (errors.hasErrors()) {
            log.error("짧은 URL 생성 요청 오류: {}", errors.getAllErrors());
            return ApiResponse.error(ApiResponseError.ERROR_DEFAULT);
        }

        log.info("짧은 URL 생성 요청: {}", request.getData().getOriginUrl());
        ShortUrlResponseDTO responseDTO = shortUrlService.createShortUrl(request.getData());

        return ApiResponse.success("짧은 URL이 생성되었습니다.", responseDTO);
    }

    /**
     * short URL 리다이렉트
     */
    @Operation(summary = "short URL 리다이렉트", description = "short URL을 입력받아 리다이렉트합니다")
    @GetMapping("/re/{shortHash}")
    public RedirectView redirect(
            @PathVariable("shortHash") String shortHash) {
        log.info("리다이렉트 요청 해시: {}", shortHash);
        String originUrl = shortUrlService.getOriginUrlByShortUrl(shortHash);
        return new RedirectView(originUrl);
    }

    /**
     * 짧은 URL 생성
     */
    @Operation(summary = "짧은 URL 생성", description = "원본 URL을 입력받아 짧은 URL을 생성합니다")
    @PostMapping(value = "/api/short-url/v2/create.api")
    public ApiResponse<ShortUrlResponseDTO> createShortUrlV2(
            @RequestBody @Valid ApiRequest<ShortUrlRequestDTO> request,
            Errors errors) {

        if (errors.hasErrors()) {
            log.error("짧은 URL 생성 요청 오류: {}", errors.getAllErrors());
            return ApiResponse.error(ApiResponseError.ERROR_DEFAULT);
        }

        log.info("짧은 URL 생성 요청: {}", request.getData().getOriginUrl());
        ShortUrlResponseDTO responseDTO = shortUrlService.createShortUrl(request.getData());

        return ApiResponse.success("짧은 URL이 생성되었습니다.", responseDTO);
    }

}
