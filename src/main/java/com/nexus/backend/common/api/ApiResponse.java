package com.nexus.backend.common.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nexus.backend.common.type.ApiResponseError;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 공통 API 응답 Wrapper 클래스
 * 모든 API 응답을 일관된 형식으로 반환하기 위한 제네릭 클래스
 * 
 * @param <T> 응답 데이터 타입
 */
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private boolean success;
    private String code;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    
    private ApiResponse(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 성공 응답 생성 (데이터 포함)
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "200", "SUCCESS", data);
    }
    
    /**
     * 성공 응답 생성 (데이터 없음)
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, "200", "SUCCESS", null);
    }
    
    /**
     * 성공 응답 생성 (커스텀 메시지)
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, "200", message, data);
    }
    
    /**
     * 에러 응답 생성 (ApiResponseError 사용)
     */
    public static <T> ApiResponse<T> error(ApiResponseError error) {
        return new ApiResponse<>(false, error.getCode(), error.getMessage(), null);
    }
    
    /**
     * 에러 응답 생성
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, code, message, null);
    }
    
    /**
     * 에러 응답 생성 (기본 400 에러)
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, "400", message, null);
    }
    
    /**
     * 커스텀 응답 생성
     */
    public static <T> ApiResponse<T> of(boolean success, String code, String message, T data) {
        return new ApiResponse<>(success, code, message, data);
    }
}

