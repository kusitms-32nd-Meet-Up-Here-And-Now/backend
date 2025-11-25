package com.meetup.hereandnow.integration.fixture.course;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.member.domain.Member;

public class CourseEntityFixture {

    public static Course getCourse(Member member) {
        return Course.builder()
                .courseTitle("테스트 제목")
                .courseDescription("테스트 설명")
                .coursePositive("테스트 좋은점")
                .courseNegative("테스트 나쁜점")
                .isPublic(false)
                .courseVisitDate(java.time.LocalDate.now())
                .courseVisitMember("연인")
                .courseRegion("서울")
                .member(member)
                .build();
    }

}
