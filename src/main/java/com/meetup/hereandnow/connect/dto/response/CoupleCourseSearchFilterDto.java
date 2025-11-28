package com.meetup.hereandnow.connect.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public record CoupleCourseSearchFilterDto(

        @Schema(description = "적용된 별점", example = "5")
        Integer rating,

        @Schema(description = "적용된 키워드 리스트", example = "[\"선물\", \"기념일\"]")
        List<String> keyword,

        @Schema(description = "적용된 코스 날짜 시작 범위", example = "2025-11-01")
        LocalDate startDate,

        @Schema(description = "적용된 코스 날짜 끝 범위", example = "2025-11-30")
        LocalDate endDate,

        @Schema(description = "적용된 코스 지역 구분", example = "강남")
        String region,

        @Schema(description = "적용된 코스 내 장소 업종 코드 리스트", example = "[\"AT4\", \"CT1\"]")
        List<String> placeCode,

        @Schema(description = "적용된 코스 태그 리스트", example = "[\"사진 찍기 좋아요\", \"음식이 맛있어요\"]")
        List<String> tag
) {
}
