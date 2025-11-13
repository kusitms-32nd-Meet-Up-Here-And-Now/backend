package com.meetup.hereandnow.connect.repository;

import com.meetup.hereandnow.connect.domain.CoupleCourseComment;
import com.meetup.hereandnow.course.domain.entity.Course;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoupleCourseCommentRepository extends JpaRepository<CoupleCourseComment, Long> {

    List<CoupleCourseComment> findAllByCourseOrderByCreatedAtAsc(Course course);

    int countByCourse(Course course);
}
