package com.meetup.hereandnow.course.dto.response;

public record CourseCardWithCommentDto(

        CourseCardResponseDto courseCard,

        CourseCommentResponseDto comment,

        boolean scrapped
) {
}
