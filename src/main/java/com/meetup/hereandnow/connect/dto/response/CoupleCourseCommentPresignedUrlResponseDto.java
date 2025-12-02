package com.meetup.hereandnow.connect.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CoupleCourseCommentPresignedUrlResponseDto(
        @Schema(description = "이미지 저장 경로", example = "course/{courseId}/couple/comment")
        String dirname
) {
}
