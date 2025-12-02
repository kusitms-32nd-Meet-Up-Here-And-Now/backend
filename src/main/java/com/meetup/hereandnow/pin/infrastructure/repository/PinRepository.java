package com.meetup.hereandnow.pin.infrastructure.repository;

import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.request.PlaceRatingDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PinRepository extends JpaRepository<Pin, Long> {

    @Query("""
                SELECT new com.meetup.hereandnow.place.dto.request.PlaceRatingDto(p.place.id, AVG(p.pinRating), COUNT(p))
                FROM Pin p
                WHERE p.place.id IN :placeIds
                GROUP BY p.place.id
            """)
    List<PlaceRatingDto> getPlaceRatingsByIds(@Param("placeIds") List<Long> placeIds);

    List<Pin> findAllByPlace(Place place);

    @Query(value = """
            SELECT p.* FROM (
                SELECT *, ROW_NUMBER() OVER (PARTITION BY place_id ORDER BY created_at DESC) as rn
                FROM pin WHERE place_id IN :placeIds
            ) p WHERE p.rn <= 3
            """, nativeQuery = true)
    List<Pin> find3PinsByPlaceIdsSorted(@Param("placeIds") List<Long> placeIds);
}

