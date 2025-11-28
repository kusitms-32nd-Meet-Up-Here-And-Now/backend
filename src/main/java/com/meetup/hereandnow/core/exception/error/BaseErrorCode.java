package com.meetup.hereandnow.core.exception.error;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode<T extends Exception> {

    String name();

    String getMessage();

    HttpStatus getHttpStatus();

    T toException();
}
