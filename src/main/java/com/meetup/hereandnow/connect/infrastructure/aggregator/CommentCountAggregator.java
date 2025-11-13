package com.meetup.hereandnow.connect.infrastructure.aggregator;

import com.meetup.hereandnow.connect.infrastructure.repository.CoupleCourseCommentRepository;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentCountAggregator {

    private final CourseCommentRepository courseCommentRepository;
    private final CoupleCourseCommentRepository coupleCourseCommentRepository;

    public int aggregate(Course course) {
        return courseCommentRepository.countByCourse(course)
                + coupleCourseCommentRepository.countByCourse(course);
    }
}

