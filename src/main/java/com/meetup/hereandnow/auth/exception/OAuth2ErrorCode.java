package com.meetup.hereandnow.auth.exception;

import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.exception.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OAuth2ErrorCode implements BaseErrorCode<DomainException> {
    OAUTH2_REDIRECT_ERROR(HttpStatus.BAD_REQUEST, "OAuth2 로그인 과정 중 리다이렉트 오류가 발생했습니다."),
    NOT_FOUND_AUTH_INFO(HttpStatus.NOT_FOUND, "OAuth2 로그인 정보가 존재하지 않습니다.");

    private final HttpStatus httpStatus;

    private final String message;


    @Override
    public DomainException toException() {
        return new DomainException(message, httpStatus);
    }
}
