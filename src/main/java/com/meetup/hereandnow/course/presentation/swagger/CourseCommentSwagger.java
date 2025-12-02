package com.meetup.hereandnow.course.presentation.swagger;

import com.meetup.hereandnow.core.config.swagger.ApiErrorCode;
import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.course.dto.request.CourseCommentSaveRequestDto;
import com.meetup.hereandnow.course.dto.response.CourseCommentResponseDto;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.member.exception.MemberErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Course-Comment", description = "코스 댓글 기능 관련 API")
public interface CourseCommentSwagger {

    @Operation(
            summary = "코스 상세 - 댓글 저장 API",
            description = "코스에 댓글을 저장합니다.",
            operationId = "POST /course/comment"
    )
    @ApiErrorCode({CourseErrorCode.class, MemberErrorCode.class})
    ResponseEntity<RestResponse<Void>> saveComment(
            @RequestBody CourseCommentSaveRequestDto courseCommentSaveRequestDto
    );

    @Operation(
            summary = "코스 상세 - 댓글 조회 API",
            description = "코스 상세 화면에서 댓글을 조회합니다.",
            operationId = "GET /course/comment/{courseId}"
    )
    @ApiErrorCode({CourseErrorCode.class})
    ResponseEntity<RestResponse<CourseCommentResponseDto>> getCommentList(
            @PathVariable @Parameter(name = "코스 식별자") Long courseId
    );
}
