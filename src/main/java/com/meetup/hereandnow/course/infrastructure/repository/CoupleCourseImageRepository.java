package com.meetup.hereandnow.course.infrastructure.repository;

import com.meetup.hereandnow.course.domain.entity.CoupleCourseImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoupleCourseImageRepository extends JpaRepository<CoupleCourseImage, Long> {
}
