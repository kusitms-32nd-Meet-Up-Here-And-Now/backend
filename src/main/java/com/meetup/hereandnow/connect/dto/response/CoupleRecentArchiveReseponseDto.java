package com.meetup.hereandnow.connect.dto.response;

import com.meetup.hereandnow.course.domain.entity.Course;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.List;

public record CoupleRecentArchiveReseponseDto(

        @Schema(description = "코스 ID", example = "1")
        Long courseId,

        @Schema(description = "코스 방문 날짜", example = "2025-11-05")
        LocalDate courseVisitDate,

        @Schema(description = "코스 제목", example = "성수동 주말, 오랜만에 만난 친구와 완벽한 하루")
        String courseTitle,

        @Schema(description = "코스 이미지 리스트 (최대 3)",
                example = "[\"https://kr.../a558.jpg\", \"https://kr.../a5eh.jpg\", \"https://kr.../a5eh.jpg\"]")
        List<String> courseImages,

        @Schema(description = "코스 설명", example = "처음 가본 성수동은 신기한 동네다. 한국인데 해외같고, 음식도 다 맛있어서 또 오고 싶어!")
        String courseDescription,

        @Schema(description = "코스 태그 리스트 (최대 3)",
                example = "[\"사진 찍기 좋아요\", \"음식이 맛있어요\", \"시설이 깨끗해요\"]")
        List<String> courseTags,

        @Schema(description = "코스 댓글 수", example = "4")
        Integer commentCount
) {
    public static CoupleRecentArchiveReseponseDto from(Course course, List<String> courseImages, Integer commentCount) {
        return new CoupleRecentArchiveReseponseDto(
                course.getId(),
                course.getCourseVisitDate(),
                course.getCourseTitle(),
                courseImages,
                course.getCourseDescription(),
                course.getCourseTags(),
                commentCount
        );
    }
}
