package com.meetup.hereandnow.course.infrastructure.repository;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.domain.entity.CourseComment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseCommentRepository extends JpaRepository<CourseComment, Long> {

    List<CourseComment> findByCourse(Course course);

    int countByCourse(Course course);

    @Query("SELECT c FROM CourseComment c JOIN FETCH c.member m WHERE c.course.id IN :courseIds")
    List<CourseComment> findByCourseIdsWithMember(@Param("courseIds") List<Long> courseIds);
}
