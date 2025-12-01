package com.meetup.hereandnow.connect.exception;

import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.exception.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CoupleCourseCommentErrorCode implements BaseErrorCode<DomainException> {
    NOT_SAVED_IMAGE(HttpStatus.NOT_FOUND, "이미지가 저장되지 않았습니다."),
    NOT_IMAGE_COMMENT(HttpStatus.BAD_REQUEST, "이미지 댓글이 아닙니다."),
    FORBIDDEN_COMMENT_DELETE(HttpStatus.FORBIDDEN, "본인의 댓글만 삭제할 수 있습니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."),
    NOT_EQUAL_COURSE_COUPLE(HttpStatus.BAD_REQUEST, "코스에 해당하는 커플이 아닙니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public DomainException toException() {
        return new DomainException(message, httpStatus);
    }
}
