package com.meetup.hereandnow.connect.presentation.swagger.comment;

import com.meetup.hereandnow.connect.dto.request.CoupleCourseImageCommentRequestDto;
import com.meetup.hereandnow.connect.dto.request.CoupleCourseTextCommentRequestDto;
import com.meetup.hereandnow.connect.dto.response.CoupleCourseCommentPresignedUrlResponseDto;
import com.meetup.hereandnow.connect.exception.CoupleCourseCommentErrorCode;
import com.meetup.hereandnow.core.config.swagger.ApiErrorCode;
import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.member.exception.MemberErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Connecting-Comment", description = "커넥팅 코스 상세 댓글 관련 API")
public interface CoupleCourseCommentSaveSwagger {

    @Operation(
            summary = "커넥팅 코스 상세 - 커플 댓글 텍스트 업로드 API",
            description = "이미지 업로드 완료 후 댓글 DB에 저장하기 위해 요청합니다. (댓글 작성 완료)",
            operationId = "POST /couple/comment/text"
    )
    @ApiErrorCode({MemberErrorCode.class, CourseErrorCode.class})
    ResponseEntity<Void> saveTextComment(
            @RequestBody CoupleCourseTextCommentRequestDto coupleCourseTextCommentRequestDto
    );

    @Operation(
            summary = "커넥팅 코스 상세 - 커플 댓글 이미지 업로드 API",
            description = "이미지 업로드 완료 후 댓글 DB에 저장하기 위해 요청합니다. (댓글 작성 완료)",
            operationId = "POST /couple/comment/image"
    )
    @ApiErrorCode({MemberErrorCode.class, CourseErrorCode.class, CoupleCourseCommentErrorCode.class})
    ResponseEntity<Void> saveImageComment(
            @RequestBody CoupleCourseImageCommentRequestDto coupleCourseImageCommentRequestDto
    );

    @Operation(
            summary = "커넥팅 코스 상세 - 커플 댓글 이미지 업로드를 위한 dirname 반환 API",
            description = "이미지를 업로드 하기위한 dirname을 반환합니다.",
            operationId = "GET /couple/comment/pre-signed/{courseId}"
    )
    @ApiErrorCode({MemberErrorCode.class, CourseErrorCode.class})
    ResponseEntity<RestResponse<CoupleCourseCommentPresignedUrlResponseDto>> getPresignedUrl(
            @PathVariable Long courseId
    );
}
