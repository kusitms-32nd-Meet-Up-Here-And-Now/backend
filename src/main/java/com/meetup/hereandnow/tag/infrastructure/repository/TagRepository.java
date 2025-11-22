package com.meetup.hereandnow.tag.infrastructure.repository;

import com.meetup.hereandnow.tag.domain.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("""
            SELECT t FROM Tag t
            JOIN FETCH t.placeGroup pg
            JOIN FETCH t.tagValue tv
            WHERE pg.code = :groupCode AND tv.name IN :tagNames
            """)
    List<Tag> findByPlaceGroupCodeAndTagNames(
            @Param("groupCode") String groupCode,
            @Param("tagNames") Collection<String> tagNames
    );
}
