package com.meetup.hereandnow.tag.infrastructure.repository;

import com.meetup.hereandnow.tag.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("""
            SELECT t
            FROM Tag t
            JOIN FETCH t.placeGroup pg
            JOIN FETCH t.tagValue tv
            WHERE pg.code IN (:placeGroupCodes)
              AND tv.name IN (:tagNames)
            """)
    List<Tag> findByPlaceGroupCodesAndTagNames(
            @Param("placeGroupCodes") Set<String> placeGroupCodes,
            @Param("tagNames") Set<String> tagNames
    );
}
