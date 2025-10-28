package com.meetup.hereandnow.course.infrastructure.repository;

import com.meetup.hereandnow.course.domain.entity.CoupleCourseRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoupleCourseRecordRepository extends JpaRepository<CoupleCourseRecord, Long> {
}
