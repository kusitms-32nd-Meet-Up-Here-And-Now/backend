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

    @Query("""
            SELECT pi FROM PinImage pi
            JOIN FETCH pi.pin pin
            WHERE pin.place.id in :placeIds
            AND pi.id = (
                SELECT MAX (pi2.id) FROM PinImage pi2
                JOIN pi2.pin p2
                WHERE p2.place.id = pin.place.id
            )
            """)
    List<PinImage> findRecentImagesByPlaceIds(@Param("placeIds") List<Long> placeIds);
}
