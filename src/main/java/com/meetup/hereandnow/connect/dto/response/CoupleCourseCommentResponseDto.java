package com.meetup.hereandnow.connect.dto.response;

import com.meetup.hereandnow.connect.domain.CoupleCourseComment;
import com.meetup.hereandnow.connect.domain.CoupleCourseImageComment;
import com.meetup.hereandnow.connect.domain.CoupleCourseTextComment;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

public record CoupleCourseCommentResponseDto(
        @Schema(description = "댓글 식별자", example = "1")
        Long commentId,

        @Schema(description = "댓글 타입", example = "IMAGE or TEXT")
        String type,

        @Schema(description = "댓글 내용 (TEXT)", example = "너무너무 재밌었다")
        String content,

        @Schema(description = "댓글 이미지 url (IMAGE)", example = "http://~")
        String imageUrl,

        @Schema(description = "댓글 작성자 식별자", example = "1")
        Long memberId,

        @Schema(description = "댓글 작성자 이름", example = "테스트 유저1")
        String writerUsername,

        @Schema(description = "댓글 작성 시간", example = "2025.11.02+09:00")
        LocalDateTime createdAt
) {
    public static CoupleCourseCommentResponseDto from(CoupleCourseComment comment) {
        if (comment instanceof CoupleCourseTextComment text) {
            return new CoupleCourseCommentResponseDto(
                    comment.getId(),
                    "TEXT",
                    text.getContent(),
                    null,
                    comment.getMember().getId(),
                    comment.getMember().getUsername(),
                    comment.getCreatedAt()
            );
        } else if (comment instanceof CoupleCourseImageComment image) {
            return new CoupleCourseCommentResponseDto(
                    comment.getId(),
                    "IMAGE",
                    null,
                    image.getImageUrl(),
                    comment.getMember().getId(),
                    comment.getMember().getUsername(),
                    comment.getCreatedAt()
            );
        }
        throw new IllegalArgumentException("Unsupported comment type");
    }
}
