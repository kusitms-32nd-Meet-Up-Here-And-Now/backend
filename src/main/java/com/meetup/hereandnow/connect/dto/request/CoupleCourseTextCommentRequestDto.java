package com.meetup.hereandnow.connect.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CoupleCourseTextCommentRequestDto(
        @Schema(description = "코스 식별자", example = "1")
        Long courseId,

        @Schema(description = "코멘트 내용", example = "여기 너무 좋았어")
        String content
) {
}
