package com.meetup.hereandnow.pin.infrastructure.repository;

import com.meetup.hereandnow.pin.domain.entity.PinTag;
import com.meetup.hereandnow.place.dto.request.PlaceTagDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PinTagRepository extends JpaRepository<PinTag, Long> {

    @Query("""
                SELECT new com.meetup.hereandnow.place.dto.request.PlaceTagDto(
                    p.place.id,
                    pt.tag.tagValue.name,
                    COUNT(pt)
                )
                FROM PinTag pt
                JOIN pt.pin p
                WHERE p.place.id IN :placeIds
                GROUP BY p.place.id, pt.tag.tagValue.name
                ORDER BY p.place.id ASC, COUNT(pt) DESC
            """)
    List<PlaceTagDto> getPinTagsByPlaceIds(@Param("placeIds") List<Long> placeIds);
}

