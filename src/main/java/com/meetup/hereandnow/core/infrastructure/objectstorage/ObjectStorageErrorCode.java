package com.meetup.hereandnow.core.infrastructure.objectstorage;

import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.exception.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ObjectStorageErrorCode implements BaseErrorCode<DomainException> {

    ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 오브젝트 스토리지에 접근할 권한이 없습니다."),
    OBJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 오브젝트를 찾을 수 없습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "요청이 AWS S3이 요구하는 형식에 맞지 않습니다."),
    AWS_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AWS S3 내부에서 오류가 발생했습니다."),
    OBJECT_URI_ERROR(HttpStatus.BAD_REQUEST, "오브젝트의 URL 형식이 잘못되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public DomainException toException() {
        return new DomainException(message, httpStatus);
    }
}
