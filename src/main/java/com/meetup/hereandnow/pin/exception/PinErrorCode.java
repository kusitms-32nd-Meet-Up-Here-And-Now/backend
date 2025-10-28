package com.meetup.hereandnow.pin.exception;

import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.exception.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PinErrorCode implements BaseErrorCode<DomainException> {
    NOT_FOUND_PIN_IMAGE(HttpStatus.NOT_FOUND, "핀 이미지를 찾을 수 없습니다."),
    NOT_FOUND_COUPLE_PIN_IMAGE(HttpStatus.NOT_FOUND, "저장 된 커플 기록 핀 이미지를 찾을 수 없습니다."),
    NOT_FOUND_PLACE(HttpStatus.NOT_FOUND, "핀에 대응하는 장소를 찾을 수 없습니다."),
    NOT_EQUAL_PIN_IMAGE_SIZE(HttpStatus.BAD_REQUEST, "핀 리스트 사이즈와 이미지 리스트 사이즈가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public DomainException toException() {
        return new DomainException(message, httpStatus);
    }
}
