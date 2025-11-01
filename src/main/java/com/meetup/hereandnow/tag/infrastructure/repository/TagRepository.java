package com.meetup.hereandnow.tag.infrastructure.repository;

import com.meetup.hereandnow.tag.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query(value = """
            SELECT t
            FROM Tag t
            JOIN PlaceGroup pg ON pg.id = t.placeGroup.id
            JOIN TagValue tv ON t.tagValue.id = tv.id
            WHERE pg.code=(:placeGroupCode) AND tv.name=(:tagName)
            """)
    Optional<Tag> findByPlaceGroupAndTagName(
            @Param("placeGroupCode") String placeGroupCode,
            @Param("tagName") String tagName
    );
}
