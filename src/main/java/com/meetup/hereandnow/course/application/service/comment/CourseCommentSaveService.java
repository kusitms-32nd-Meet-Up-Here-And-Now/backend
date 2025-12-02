package com.meetup.hereandnow.course.application.service.comment;

import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.domain.entity.CourseComment;
import com.meetup.hereandnow.course.dto.request.CourseCommentSaveRequestDto;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.course.infrastructure.repository.CourseCommentRepository;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseCommentSaveService {

    private final CourseRepository courseRepository;
    private final CourseCommentRepository courseCommentRepository;

    @Transactional
    public void saveCourseComment(CourseCommentSaveRequestDto courseCommentSaveRequestDto) {

        Member member = SecurityUtils.getCurrentMember();
        Course course = courseRepository.findById(courseCommentSaveRequestDto.courseId())
                .orElseThrow(CourseErrorCode.NOT_FOUND_COURSE::toException);

        CourseComment courseComment = CourseComment.builder()
                .course(course)
                .member(member)
                .content(courseCommentSaveRequestDto.content())
                .build();

        courseCommentRepository.save(courseComment);
    }
}
