package com.meetup.hereandnow.course.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CourseCommentResponseDto(

        @Schema(description = "댓글 총 개수", example = "4")
        int count,

        @Schema(description = "댓글 리스트")
        List<CourseCommentDto> comments

) {

}
