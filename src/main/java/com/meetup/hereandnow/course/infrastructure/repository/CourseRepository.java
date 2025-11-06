package com.meetup.hereandnow.course.infrastructure.repository;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.member.domain.Member;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Course c WHERE c.id = :id")
    Optional<Course> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT c.id FROM Course c WHERE c.member = :member ORDER BY c.createdAt DESC")
    Page<Long> findCourseIdsByMember(Member member, Pageable pageable);

    @Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.pinList WHERE c.id IN :ids ORDER BY c.createdAt DESC")
    List<Course> findWithPinsByIds(@Param("ids") List<Long> ids);

    @Query(value = "SELECT * FROM course c WHERE c.member_id = (:memberId) ORDER BY c.created_at DESC LIMIT 1",
            nativeQuery = true)
    Optional<Course> findByMemberOrderByCreatedAtDesc(@Param("memberId") Long memberId);

    @Query("""
            SELECT DISTINCT c FROM Course c
            LEFT JOIN FETCH c.pinList p
            LEFT JOIN FETCH p.place pl
            LEFT JOIN FETCH p.pinImages pi
            WHERE c.id = :courseId
            ORDER BY p.id ASC
            """)
    Optional<Course> findCourseDetailsById(@Param("courseId") Long courseId);
}
