package com.meetup.hereandnow.course.dto.response;

import com.meetup.hereandnow.course.domain.entity.CourseComment;
import io.swagger.v3.oas.annotations.media.Schema;

public record CourseCommentDto(

        @Schema(description = "댓글 식별자", example = "1")
        Long commentId,

        @Schema(description = "작성자 이름", example = "김히어")
        String nickName,

        @Schema(description = "작성자 프로필 이미지", example = "http://~~")
        String profileImage,

        @Schema(description = "댓글 내용", example = "여기 장소 좋아요.")
        String content

) {
    public static CourseCommentDto from(CourseComment comment) {
        return new CourseCommentDto(
                comment.getId(),
                maskNickname(comment.getMember().getNickname()),
                comment.getMember().getProfileImage(),
                comment.getContent()
        );
    }

    private static String maskNickname(String nickname) {
        return nickname.charAt(0) +
                "*".repeat(nickname.length() - 1);
    }
}

