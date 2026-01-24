package com.nexus.backend.api.shortUrl.controller;

import com.nexus.backend.api.shortUrl.dto.ShortUrlRequestDTO;
import com.nexus.backend.api.shortUrl.dto.ShortUrlResponseDTO;
import com.nexus.backend.api.shortUrl.dto.ShortUrlStatsRequestDTO;
import com.nexus.backend.api.shortUrl.dto.ShortUrlStatsResponseDTO;
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

/**
 * short url 관리 컨트롤러
 */
@Tag(name = "short url 관리 컨트롤러", description = "short url API")
@Slf4j
@RestController
@RequestMapping(value = "/api/short-url")
@RequiredArgsConstructor
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    /**
     * short URL 생성
     */
    @Operation(summary = "short URL 생성", description = "원본 URL을 입력받아 짧은 URL을 생성합니다")
    @PostMapping(value = "/create")
    public ApiResponse<ShortUrlResponseDTO> createShortUrl(
            @RequestBody @Valid ApiRequest<ShortUrlRequestDTO> request,
            Errors errors) {

        if (errors.hasErrors()) {
            log.error("short URL 생성 요청 오류: {}", errors.getAllErrors());
            return ApiResponse.error(ApiResponseError.ERROR_DEFAULT);
        }

        log.info("short URL 생성 요청: {}", request.getData().getOriginUrl());
        ShortUrlResponseDTO responseDTO = shortUrlService.createShortUrl(request.getData());

        return ApiResponse.success("short URL이 생성되었습니다.", responseDTO);
    }

    /**
     * KPI 데이터 조회
     */
    @Operation(summary = "KPI 데이터 조회", description = "KPI 데이터를 조회합니다")
    @PostMapping(value = "/stats/kpi")
    public ApiResponse<ShortUrlStatsResponseDTO.Kpi> getKpiData(
            @RequestBody @Valid ApiRequest<ShortUrlStatsRequestDTO> request,
            Errors errors) {

        if (errors.hasErrors()) {
            log.error("KPI 데이터 조회 요청 오류: {}", errors.getAllErrors());
            return ApiResponse.error(ApiResponseError.ERROR_DEFAULT);
        }

        log.info("KPI 데이터 조회 요청: {}", request.getData());
        ShortUrlStatsResponseDTO.Kpi responseDTO = shortUrlService.getKpi(request);

        return ApiResponse.success("KPI 데이터가 조회되었습니다.", responseDTO);
    }

    /**
     * 추이 차트 데이터 조회
     */
     @Operation(summary = "Trend 차트 데이터 조회", description = "Trend 차트 데이터를 조회합니다")
     @GetMapping(value = "/stats/trend")
     public ApiResponse<ShortUrlStatsResponseDTO.Trend> getTrendData(
             @RequestBody @Valid ApiRequest<ShortUrlStatsRequestDTO> request,
             Errors errors) {

         if (errors.hasErrors()) {
             log.error("추이 차트 데이터 조회 요청 오류: {}", errors.getAllErrors());
             return ApiResponse.error(ApiResponseError.ERROR_DEFAULT);
         }

         log.info("추이 차트 데이터 조회 요청: {}", request.getData().getPeriod());
         ShortUrlStatsResponseDTO.Trend responseDTO = shortUrlService.getTrend(request);

         return ApiResponse.success("추이 차트 데이터가 조회되었습니다.", responseDTO);
     }

    /**
     * 링크 점유율 데이터 조회 (Grid용)
     */
    // @Operation(summary = "링크 점유율 데이터 조회", description = "링크 점유율 데이터를 조회합니다")
    // @GetMapping(value = "/stats/usage")
    // public ApiResponse<ShortUrlResponseDTO> getUsageData(
    //         @RequestBody @Valid ApiRequest<ShortUrlRequestDTO> request,
    //         Errors errors) {

    //     if (errors.hasErrors()) {
    //         log.error("링크 점유율 데이터 조회 요청 오류: {}", errors.getAllErrors());
    //         return ApiResponse.error(ApiResponseError.ERROR_DEFAULT);
    //     }

    //     log.info("링크 점유율 데이터 조회 요청: {}", request.getData().getOriginUrl());
    //     ShortUrlResponseDTO responseDTO = shortUrlService.createShortUrl(request.getData());

    //     return ApiResponse.success("링크 점유율 데이터가 조회되었습니다.", responseDTO);
    // }

    /**
     * 전체 통계 데이터 일괄 조회
     */
    // @Operation(summary = "전체 통계 데이터 일괄 조회", description = "전체 통계 데이터를 일괄 조회합니다")
    // @GetMapping(value = "/stats/all")
    // public ApiResponse<ShortUrlResponseDTO> getAllStatsData(
    //         @RequestBody @Valid ApiRequest<ShortUrlRequestDTO> request,
    //         Errors errors) {

    //     if (errors.hasErrors()) {
    //         log.error("전체 통계 데이터 일괄 조회 요청 오류: {}", errors.getAllErrors());
    //         return ApiResponse.error(ApiResponseError.ERROR_DEFAULT);
    //     }

    //     log.info("전체 통계 데이터 일괄 조회 요청: {}", request.getData().getOriginUrl());
    //     ShortUrlResponseDTO responseDTO = shortUrlService.createShortUrl(request.getData());

    //     return ApiResponse.success("전체 통계 데이터가 일괄 조회되었습니다.", responseDTO);
    // }

}
