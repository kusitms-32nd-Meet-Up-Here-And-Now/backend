package com.meetup.hereandnow.course.infrastructure.mapper;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.request.CourseSaveDto;
import com.meetup.hereandnow.member.domain.Member;

import java.math.BigDecimal;

public class CourseMapper {

    private CourseMapper() {
        throw new UnsupportedOperationException("this is util class and cannot be instantiated");
    }

    public static Course toEntity(CourseSaveDto dto, Member member) {
        return Course.builder()
                .courseVisitDate(dto.courseVisitDate())
                .courseVisitMember(dto.courseWith())
                .courseRegion(dto.courseRegion())
                .courseTitle(dto.courseTitle())
                .courseDescription(dto.courseDescription())
                .isPublic(dto.isPublic())
                .coursePositive(dto.coursePositive())
                .courseNegative(dto.courseNegative())
                .member(member)
                .courseRating(BigDecimal.valueOf(dto.courseRating()))
                .build();
    }
}
