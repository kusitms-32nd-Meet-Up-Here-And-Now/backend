package com.meetup.hereandnow.archive.dto.response;

import com.meetup.hereandnow.course.domain.entity.Course;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record CourseFolderResponseDto(

        @Schema(description = "코스 ID", example = "1")
        Long id,

        @Schema(description = "코스 제목", example = "강남 친구들과")
        String courseTitle,

        @Schema(description = "코스 댓글 수", example = "4")
        Integer commentCount,

        @Schema(description = "코스 방문 날짜", example = "2025-11-05")
        LocalDate courseVisitDate
) {
    public static CourseFolderResponseDto from(Course course, int commentCount) {
        return new CourseFolderResponseDto(
                course.getId(),
                course.getCourseTitle(),
                commentCount,
                course.getCourseVisitDate()
        );
    }
}
