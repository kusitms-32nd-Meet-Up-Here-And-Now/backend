package com.meetup.hereandnow.course.domain.value;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseTagEnum {

    COZY("편안함"),
    EXCITED("흥미로운");

    private final String name;
}
