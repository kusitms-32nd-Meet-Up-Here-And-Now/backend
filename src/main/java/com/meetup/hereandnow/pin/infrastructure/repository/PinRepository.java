package com.meetup.hereandnow.pin.infrastructure.repository;

import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.place.dto.PlaceRatingDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PinRepository extends JpaRepository<Pin, Long> {

    @Query("""
                SELECT new com.meetup.hereandnow.place.dto.PlaceRatingDto(p.place.id, AVG(p.pinRating), COUNT(p))
                FROM Pin p
                WHERE p.place.id IN :placeIds
                GROUP BY p.place.id
            """)
    List<PlaceRatingDto> getPlaceRatingsByIds(@Param("placeIds") List<Long> placeIds);
}

