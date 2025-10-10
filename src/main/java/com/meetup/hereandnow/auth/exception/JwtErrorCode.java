package com.meetup.hereandnow.auth.exception;

import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.exception.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode implements BaseErrorCode<DomainException> {

    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),
    TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "JWT 토큰이 유효하지 않습니다."),
    TOKEN_SIG_INVALID(HttpStatus.UNAUTHORIZED, "JWT 서명이 잘못되었습니다."),
    TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "지원되지 않는 JWT 토큰입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public DomainException toException() {
        return new DomainException(message, httpStatus);
    }
}
