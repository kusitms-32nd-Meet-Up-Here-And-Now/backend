package com.meetup.hereandnow.connect.domain.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseVisitType {
    COUPLE("연인"),
    FRIEND("친구"),
    FAMILY("가족"),
    ALONE("혼자");

    private final String value;
}

