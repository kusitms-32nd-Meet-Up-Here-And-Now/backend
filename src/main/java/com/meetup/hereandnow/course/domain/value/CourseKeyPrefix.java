package com.meetup.hereandnow.course.domain.value;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseKeyPrefix {

    COURSE_KEY_PREFIX("courseKey:");

    private final String prefix;

    public String key(String suffix) {
        return prefix + suffix;
    }
}
