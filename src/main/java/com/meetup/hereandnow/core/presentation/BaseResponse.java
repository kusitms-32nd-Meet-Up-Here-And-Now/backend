package com.meetup.hereandnow.core.presentation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseResponse {

    private Boolean isSuccess;

    private final LocalDateTime timestamp;
}
