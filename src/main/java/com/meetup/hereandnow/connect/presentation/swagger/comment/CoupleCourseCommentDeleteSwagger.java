package com.meetup.hereandnow.connect.presentation.swagger.comment;

import com.meetup.hereandnow.connect.exception.CoupleCourseCommentErrorCode;
import com.meetup.hereandnow.core.config.swagger.ApiErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Connecting-Comment", description = "커넥팅 코스 상세 댓글 관련 API")
public interface CoupleCourseCommentDeleteSwagger {

    @Operation(
            summary = "커넥팅 코스 상세 - 이미지 댓글 삭제 API",
            description = "커넥팅 코스 상세 페이지에서 이미지로 된 댓글을 삭제한다.",
            operationId = "DELETE /couple/comment/{commentId}"
    )
    @ApiErrorCode({CoupleCourseCommentErrorCode.class})
    ResponseEntity<Void> deleteImageComment(
            @PathVariable Long commentId
    );
}
