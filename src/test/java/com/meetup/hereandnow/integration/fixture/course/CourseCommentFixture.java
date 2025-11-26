package com.meetup.hereandnow.integration.fixture.course;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.domain.entity.CourseComment;
import com.meetup.hereandnow.integration.fixture.member.MemberEntityFixture;
import com.meetup.hereandnow.member.domain.Member;

public class CourseCommentFixture {

    public static CourseComment getCourseComment(Course course) {
        return getCourseComment(course, MemberEntityFixture.getMember());
    }

    public static CourseComment getCourseComment(Course course, Member member) {
        return CourseComment.builder()
                .content("정말 좋은 코스네요!")
                .course(course)
                .member(member)
                .build();
    }
}
