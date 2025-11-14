package com.meetup.hereandnow.scrap.infrastructure.repository;

import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.scrap.domain.CourseScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CourseScrapRepository extends JpaRepository<CourseScrap, Long> {

    Optional<CourseScrap> findByMemberIdAndCourseId(Long memberId, Long courseId);

    @Query(value = """
            SELECT cs FROM CourseScrap cs
            JOIN FETCH cs.course c
            JOIN FETCH c.member m
            WHERE cs.member = :member
            """,
            countQuery = "SELECT COUNT(cs) FROM CourseScrap cs WHERE cs.member = :member")
    Page<CourseScrap> findScrapsByMemberWithSort(@Param("member") Member member, Pageable pageable);

    @Query("SELECT cs.course.id FROM CourseScrap cs WHERE cs.member = :member AND cs.course.id IN :courseIds")
    Set<Long> findScrappedCourseIdsByMemberAndCourseIds(@Param("member") Member member, @Param("courseIds") List<Long> courseIds);
}
