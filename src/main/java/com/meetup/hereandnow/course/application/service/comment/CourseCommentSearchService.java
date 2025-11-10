package com.meetup.hereandnow.course.application.service.comment;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.domain.entity.CourseComment;
import com.meetup.hereandnow.course.dto.response.CourseCommentDto;
import com.meetup.hereandnow.course.dto.response.CourseCommentResponseDto;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.course.infrastructure.repository.CourseCommentRepository;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseCommentSearchService {

    private final CourseCommentRepository courseCommentRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public CourseCommentResponseDto getCommentList(Long courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(CourseErrorCode.NOT_FOUND_COURSE::toException);

        List<CourseComment> commentList = courseCommentRepository.findByCourse(course);

        List<CourseCommentDto> dtoList = commentList.stream()
                .map(CourseCommentDto::from)
                .toList();

        return new CourseCommentResponseDto(dtoList.size(), dtoList);
    }
}
