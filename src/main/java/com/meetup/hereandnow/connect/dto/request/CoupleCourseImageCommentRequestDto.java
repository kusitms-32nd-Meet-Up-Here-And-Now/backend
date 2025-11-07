package com.meetup.hereandnow.connect.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CoupleCourseImageCommentRequestDto(
        @Schema(description = "코스 식별자", example = "1")
        Long courseId,

        @Schema(description = "이미지 objectKey", example = "course/{courseId}/couple/...")
        String objectKey
) {
}
