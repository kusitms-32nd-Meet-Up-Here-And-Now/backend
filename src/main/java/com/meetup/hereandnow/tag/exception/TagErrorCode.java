package com.meetup.hereandnow.tag.exception;

import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.exception.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TagErrorCode implements BaseErrorCode<DomainException> {
    NOT_FOUND_TAG_DATA(HttpStatus.NOT_FOUND, "요청한 장소 분류 및 태그 이름에 맞는 태그를 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public DomainException toException() {
        return new DomainException(message, httpStatus);
    }
}
