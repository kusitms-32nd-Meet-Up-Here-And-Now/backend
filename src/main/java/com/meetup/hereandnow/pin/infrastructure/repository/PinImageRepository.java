package com.meetup.hereandnow.pin.infrastructure.repository;

import com.meetup.hereandnow.pin.domain.entity.PinImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PinImageRepository extends JpaRepository<PinImage, Long> {

    @Query("""
            SELECT img.imageUrl
            FROM PinImage img
            WHERE img.pin.course.id = (:courseId)
            """)
    List<String> findImageUrlsByCourseId(@Param("courseId") Long courseId);
}
