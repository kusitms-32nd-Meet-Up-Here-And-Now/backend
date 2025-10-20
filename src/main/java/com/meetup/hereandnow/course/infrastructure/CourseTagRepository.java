package com.meetup.hereandnow.course.infrastructure;

import com.meetup.hereandnow.course.domain.entity.CourseTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseTagRepository extends JpaRepository<CourseTag, Long> {

}
