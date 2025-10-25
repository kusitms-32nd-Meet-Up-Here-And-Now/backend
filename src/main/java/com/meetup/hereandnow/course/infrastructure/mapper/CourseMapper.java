package com.meetup.hereandnow.course.infrastructure.mapper;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import com.meetup.hereandnow.member.domain.Member;
import java.math.BigDecimal;

public class CourseMapper {

    private CourseMapper() {
        throw new UnsupportedOperationException("this is util class and cannot be instantiated");
    }

    public static Course toEntity(CourseSaveDto dto, Member member, String courseThumbnailImage) {
        return Course.builder()
                .courseTitle(dto.courseTitle())
                .courseRating(BigDecimal.valueOf(dto.courseRating()))
                .courseDescription(dto.courseDescription())
                .courseThumbnailImage(courseThumbnailImage)
                .isPublic(dto.isPublic())
                .member(member)
                .build();
    }
}
