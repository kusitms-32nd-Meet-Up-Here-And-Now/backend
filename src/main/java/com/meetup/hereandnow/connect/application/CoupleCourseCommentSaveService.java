package com.meetup.hereandnow.connect.application;

import com.meetup.hereandnow.connect.domain.CoupleCourseImageComment;
import com.meetup.hereandnow.connect.domain.CoupleCourseTextComment;
import com.meetup.hereandnow.connect.repository.CoupleCourseCommentRepository;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoupleCourseCommentSaveService {

    private final CoupleCourseCommentRepository coupleCourseCommentRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public void addTextComment(Long courseId, String content) {
        Member member = getCurrentMember();
        Course course = getCourse(courseId);

        CoupleCourseTextComment comment = CoupleCourseTextComment.of(course, member, content);

        coupleCourseCommentRepository.save(comment);
    }

    @Transactional
    public void addImageComment(Long courseId, String imageUrl) {
        Member member = getCurrentMember();
        Course course = getCourse(courseId);

        CoupleCourseImageComment comment = CoupleCourseImageComment.of(course, member, imageUrl);

        coupleCourseCommentRepository.save(comment);
    }

    public String getPresignedDirname(Long courseId) {
        Member member = getCurrentMember();
        Course course = getCourse(courseId);

        return getDirname(courseId);
    }

    private String getDirname(Long courseId) {
        return "/course/" + courseId + "/comment";
    }

    private Member getCurrentMember() {
        return SecurityUtils.getCurrentMember();
    }

    private Course getCourse(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(CourseErrorCode.NOT_FOUND_COURSE::toException);
    }
}
