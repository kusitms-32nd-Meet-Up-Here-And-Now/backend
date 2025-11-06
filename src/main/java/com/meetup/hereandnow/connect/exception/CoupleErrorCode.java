package com.meetup.hereandnow.connect.exception;

import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.exception.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CoupleErrorCode implements BaseErrorCode<DomainException> {
    NOT_FOUND_COUPLE(HttpStatus.NOT_FOUND, "커플 정보를 찾을 수 없습니다."),
    IS_COUPLE_NOW(HttpStatus.CONFLICT, "이미 커플로 등록되어 있는 멤버입니다."),
    UNAUTHORIZED_REJECTION(HttpStatus.UNAUTHORIZED, "상대방만 거절할 수 있습니다."),
    UNAUTHORIZED_APPROVAL(HttpStatus.UNAUTHORIZED, "상대방만 수락할 수 있습니다."),
    ALREADY_PROCESSED(HttpStatus.CONFLICT, "이미 처리된 커플 요청입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public DomainException toException() {
        return new DomainException(message, httpStatus);
    }
}
