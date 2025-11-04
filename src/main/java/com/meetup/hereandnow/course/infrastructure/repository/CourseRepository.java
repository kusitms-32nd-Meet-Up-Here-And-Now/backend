package com.meetup.hereandnow.course.infrastructure.repository;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.member.domain.Member;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Course c WHERE c.id = :id")
    Optional<Course> findByIdWithLock(@Param("id") Long id);

    Page<Course> findByMemberOrderByCreatedAtDesc(Member member, Pageable pageable);

    Optional<Course> findByMemberOrderByCreatedAtDesc(Member member);
}
