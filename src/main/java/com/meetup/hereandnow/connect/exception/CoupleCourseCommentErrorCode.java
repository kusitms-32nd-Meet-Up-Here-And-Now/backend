package com.meetup.hereandnow.connect.exception;

import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.exception.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CoupleCourseCommentErrorCode implements BaseErrorCode<DomainException> {
    NOT_SAVED_IMAGE(HttpStatus.NOT_FOUND, "코멘트 이미지가 저장되지 않았습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public DomainException toException() {
        return new DomainException(message, httpStatus);
    }
}
