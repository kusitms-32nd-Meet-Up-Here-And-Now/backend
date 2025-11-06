package com.meetup.hereandnow.course.dto.response;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.dto.PinDetailsResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public record CourseDetailsResponseDto(

        @Schema(description = "코스 ID", example = "1")
        Long courseId,

        @Schema(description = "요청 사용자가 코스 작성자인지 여부", example = "true")
        Boolean courseWriter,

        @Schema(description = "코스 방문 날짜", example = "2025-11-05")
        LocalDate courseVisitDate,

        @Schema(description = "코스 제목", example = "성수동 주말, 오랜만에 만난 친구와 완벽한 하루")
        String courseTitle,

        @Schema(description = "코스 설명", example = "처음 가본 성수동은 신기한 동네다. 한국인데 해외같고, 음식도 다 맛있어서 또 오고 싶어!")
        String courseDescription,

        @Schema(description = "코스 태그 (최대 3개)", example = "[\"사진 찍기 좋아요\", \"음식이 맛있어요\"]")
        List<String> courseTags,

        List<PinDetailsResponseDto> pins

        // TODO: 코스 리뷰 (댓글) 추가 또는 댓글 API 분리
) {
    public static CourseDetailsResponseDto of(
            Member member,
            Course course,
            List<PinDetailsResponseDto> pins
    ) {
        return new CourseDetailsResponseDto(
                course.getId(),
                course.getMember().getId().equals(member.getId()),
                course.getCourseVisitDate(),
                course.getCourseTitle(),
                course.getCourseDescription(),
                course.getCourseTags(),
                pins
        );
    }
}
