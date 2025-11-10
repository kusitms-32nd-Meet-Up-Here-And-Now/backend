package com.meetup.hereandnow.course.infrastructure.repository;

import com.meetup.hereandnow.course.domain.entity.CourseComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCommentRepository extends JpaRepository<CourseComment, Long> {
}
