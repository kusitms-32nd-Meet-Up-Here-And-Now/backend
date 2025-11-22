package com.meetup.hereandnow.course.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CourseCommentSaveRequestDto(
        @Schema(description = "코스 식별자", example = "1")
        Long courseId,

        @Schema(description = "댓글 내용", example = "여기 장소 좋아요.")
        String content
) {
}
