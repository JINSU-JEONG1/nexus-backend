package com.nexus.backend.api.shortUrl.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShortUrlStatsRequestDTO {

    private LocalDate startDate; // 조회 시작일
    private LocalDate endDate;   // 조회 종료일
    private String unit;         // 조회 단위 (DAY, WEEK, MONTH)
}