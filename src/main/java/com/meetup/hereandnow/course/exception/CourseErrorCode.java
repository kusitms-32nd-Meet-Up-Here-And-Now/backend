package com.meetup.hereandnow.course.exception;

import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.exception.error.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CourseErrorCode implements BaseErrorCode<DomainException> {
    NOT_FOUND_COURSE_METADATA(HttpStatus.NOT_FOUND, "저장된 코스 메타데이터를 찾을 수 없습니다."),
    NOT_FOUND_COURSE_IMAGE(HttpStatus.NOT_FOUND, "저장된 코스 이미지를 찾을 수 없습니다."),
    NOT_FOUND_COUPLE_COURSE_IMAGE(HttpStatus.NOT_FOUND, "저장된 커플 기록 코스 이미지를 찾을 수 없습니다."),
    NOT_EQUAL_IMAGE_COUNT(HttpStatus.CONFLICT, "저장을 요청한 이미지 개수가 맞지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public DomainException toException() {
        return new DomainException(message, httpStatus);
    }
}
