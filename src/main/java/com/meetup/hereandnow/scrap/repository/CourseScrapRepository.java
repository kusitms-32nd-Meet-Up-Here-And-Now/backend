package com.meetup.hereandnow.scrap.repository;

import com.meetup.hereandnow.scrap.domain.CourseScrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CourseScrapRepository extends JpaRepository<CourseScrap, Long> {

    Optional<CourseScrap> findByMemberIdAndCourseId(Long memberId, Long courseId);
}
