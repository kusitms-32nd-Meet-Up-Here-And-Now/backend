package com.meetup.hereandnow.connect.application.comment;

import com.meetup.hereandnow.connect.dto.response.CoupleCourseCommentResponseDto;
import com.meetup.hereandnow.connect.infrastructure.repository.CoupleCourseCommentRepository;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoupleCourseCommentReadService {

    private final CourseRepository courseRepository;
    private final CoupleCourseCommentRepository coupleCourseCommentRepository;

    @Transactional
    public List<CoupleCourseCommentResponseDto> getComments(Long courseId) {
        SecurityUtils.getCurrentMember();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(CourseErrorCode.NOT_FOUND_COURSE::toException);

        return coupleCourseCommentRepository.findAllByCourseOrderByCreatedAtAsc(course).stream()
                .map(CoupleCourseCommentResponseDto::from)
                .toList();
    }
}
