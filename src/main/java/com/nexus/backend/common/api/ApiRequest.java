package com.nexus.backend.common.api;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공통 API 요청 Wrapper 클래스
 * 모든 API 요청을 일관된 형식으로 받기 위한 제네릭 클래스
 * 
 * @param <T> 요청 데이터 타입
 */
@Getter
@NoArgsConstructor
public class ApiRequest<T> {
    
    private T data;
    
    private ApiRequest(T data) {
        this.data = data;
    }
    
    /**
     * 요청 데이터로 ApiRequest 생성
     */
    public static <T> ApiRequest<T> of(T data) {
        return new ApiRequest<>(data);
    }
    
    /**
     * 빈 ApiRequest 생성
     */
    public static <T> ApiRequest<T> empty() {
        return new ApiRequest<>(null);
    }
}

