package com.meetup.hereandnow.core.exception.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements BaseErrorCode<RuntimeException> {

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 내부 오류입니다.");

    private final HttpStatus httpStatus;

    private final String message;

    @Override
    public RuntimeException toException() {
        return new RuntimeException(message);
    }
}
