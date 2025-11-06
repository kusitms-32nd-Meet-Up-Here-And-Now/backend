package com.meetup.hereandnow.course.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record CourseSaveDto(

        @Schema(description = "코스 제목", example = "홍대 데이트 코스 완벽 정리")
        String courseTitle,

        @Schema(description = "코스 설명", example = "홍대 놀거리 리스트")
        String courseDescription,

        @Schema(description = "코스 좋았던 점", example = "너무 느좋이에요")
        String coursePositive,

        @Schema(description = "코스 나빴던 점", example = "도로가 너무 좁아요")
        String courseNegative,

        @Schema(description = "코스 공개 여부", example = "true")
        Boolean isPublic,

        @Schema(description = "코스 방문 날짜", example = "2025.11.02")
        @JsonFormat(pattern = "yyyy.MM.dd")
        LocalDate courseVisitDate,

        @Schema(description = "코스 함께한 사람", example = "연인")
        String courseWith,

        @Schema(description = "코스 지역", example = "마포")
        String courseRegion,

        List<PinSaveDto> pinList
) {

}
