package com.meetup.hereandnow.scrap.repository;

import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.scrap.domain.CourseScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseScrapRepository extends JpaRepository<CourseScrap, Long> {

    Optional<CourseScrap> findByMemberIdAndCourseId(Long memberId, Long courseId);

    @Query(value = "SELECT cs FROM CourseScrap cs JOIN FETCH cs.course c WHERE cs.member = :member ORDER BY cs.createdAt DESC",
            countQuery = "SELECT count(cs) FROM CourseScrap cs WHERE cs.member = :member")
    Page<CourseScrap> findByMemberWithCourse(@Param("member") Member member, Pageable pageable);
}
