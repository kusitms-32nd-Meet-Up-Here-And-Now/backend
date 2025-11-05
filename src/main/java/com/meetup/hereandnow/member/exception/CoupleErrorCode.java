package com.meetup.hereandnow.member.exception;

import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.exception.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CoupleErrorCode implements BaseErrorCode<DomainException> {
    NOT_FOUND_COUPLE(HttpStatus.NOT_FOUND, "커플 정보를 찾을 수 없습니다."),
    IS_COUPLE_NOW(HttpStatus.CONFLICT, "이미 커플로 등록되어 있는 멤버입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public DomainException toException() {
        return new DomainException(message, httpStatus);
    }
}
