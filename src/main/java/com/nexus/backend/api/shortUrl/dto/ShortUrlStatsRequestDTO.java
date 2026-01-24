package com.nexus.backend.api.shortUrl.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ShortUrlStatsRequestDTO {

    private String period;         // 조회 단위 (day, week, month)
}