package com.meetup.hereandnow.connect.presentation.swagger.comment;

import com.meetup.hereandnow.connect.dto.response.CoupleCourseCommentResponseDto;
import com.meetup.hereandnow.core.presentation.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Connecting-Comment", description = "커넥팅 코스 상세 댓글 관련 API")
public interface CoupleCourseCommentReadSwagger {

    @Operation(
            summary = "커넥팅 코스 상세 - 댓글 읽기 API",
            description = "커플 코스 상세에서 커플간의 댓글을 읽습니다.",
            operationId = "GET /couple/comment/{courseId}"
    )
    ResponseEntity<RestResponse<List<CoupleCourseCommentResponseDto>>> readComment(
            @PathVariable Long courseId
    );
}
