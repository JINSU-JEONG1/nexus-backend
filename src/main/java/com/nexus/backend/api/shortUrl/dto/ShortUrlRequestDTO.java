package com.nexus.backend.api.shortUrl.dto;

import org.hibernate.validator.constraints.URL;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import jakarta.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShortUrlRequestDTO {

    @NotBlank(message = "원본 URL은 필수 항목입니다.")
    @URL(message = "유효한 URL 형식이 아닙니다.")
    private String originUrl;

    private String shortUrl;
}
