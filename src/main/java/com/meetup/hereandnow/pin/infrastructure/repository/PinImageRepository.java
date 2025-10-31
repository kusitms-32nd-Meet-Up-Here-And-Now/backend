package com.meetup.hereandnow.pin.infrastructure.repository;

import com.meetup.hereandnow.pin.domain.entity.PinImage;
import com.meetup.hereandnow.pin.dto.PlaceIdWithImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PinImageRepository extends JpaRepository<PinImage, Long> {

    @Query(value = """
            WITH SelectedPlaceImages AS (
                SELECT
                    img.image_url AS imageUrl,
                    pin.place_id AS placeId,
                    ROW_NUMBER() OVER(
                        PARTITION BY pin.place_id -- Place id 별로 이미지 나눠서 그 안에서 번호 붙임
                        ORDER BY img.id DESC
                    ) as row_number
                FROM pin_image img
                JOIN pin ON img.pin_id = pin.id
                WHERE pin.place_id IN (:placeIds)
            )
            SELECT placeId, imageUrl
            FROM SelectedPlaceImages
            WHERE row_number <= 3
            ORDER BY placeId, row_number
            """, nativeQuery = true)
    List<PlaceIdWithImage> findImageUrlsByPlaceIds(@Param("placeIds") List<Long> placeIds);
}
