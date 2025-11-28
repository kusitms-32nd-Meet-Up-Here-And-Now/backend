package com.meetup.hereandnow.course.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record CommitSaveCourseResponseDto(
        @Schema(description = "저장된 코스 ID", example = "1")
        Long courseId,

        @Schema(description = "저장 완료 메시지", example = "저장이 완료되었습니다.")
        String message
) {

    public static CommitSaveCourseResponseDto of(Long courseId) {
        return new CommitSaveCourseResponseDto(courseId, "저장이 완료되었습니다.");
    }
}
