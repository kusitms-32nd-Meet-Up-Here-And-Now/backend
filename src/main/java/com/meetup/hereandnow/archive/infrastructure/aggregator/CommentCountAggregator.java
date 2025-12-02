package com.meetup.hereandnow.archive.infrastructure.aggregator;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentCountAggregator {

    private final CourseCommentRepository courseCommentRepository;

    public int aggregate(Course course) {
        return courseCommentRepository.countByCourse(course);
    }
}

