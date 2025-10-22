package com.meetup.hereandnow.place.infrastructure.repository;

import com.meetup.hereandnow.place.domain.Place;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query(value = "SELECT * FROM place p WHERE p.place_name = :name AND ST_X(p.location::geometry) = :lon AND ST_Y(p.location::geometry) = :lat", nativeQuery = true)
    Optional<Place> findByNameAndCoordinates(@Param("name") String name, @Param("lat") double lat, @Param("lon") double lon);
}
