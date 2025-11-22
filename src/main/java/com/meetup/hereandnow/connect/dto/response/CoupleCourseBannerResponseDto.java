package com.meetup.hereandnow.connect.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record CoupleCourseBannerResponseDto(

        @Schema(description = "코스 식별자", example = "1")
        Long courseId,

        @Schema(description = "코스 방문 날짜", example = "2025-11-02")
        @JsonFormat(shape = Shape.STRING, pattern = "yyyy.MM.dd")
        LocalDate startDate,

        @Schema(description = "코스 제목", example = "홍대는 재밌었다")
        String courseTitle,

        @Schema(description = "코스 설명", example = "홍대 커피숍 좋았다.")
        String courseDescription,

        @Schema(description = "장소 개수", example = "4")
        int placeCount,

        @Schema(description = "코스 썸네일 이미지", example = "http://~~")
        String thumbnailImageLink
) {
        public static CoupleCourseBannerResponseDto from(
                Course course, String imageUrl
        ) {
                return new CoupleCourseBannerResponseDto(
                        course.getId(),
                        course.getCourseVisitDate(),
                        course.getCourseTitle(),
                        course.getCourseDescription(),
                        course.getPinList().size(),
                        imageUrl
                );
        }

}
