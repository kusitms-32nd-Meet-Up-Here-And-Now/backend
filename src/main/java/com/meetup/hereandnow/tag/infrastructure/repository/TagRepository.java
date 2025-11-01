package com.meetup.hereandnow.tag.infrastructure.repository;

import com.meetup.hereandnow.tag.domain.entity.PlaceGroup;
import com.meetup.hereandnow.tag.domain.entity.Tag;
import com.meetup.hereandnow.tag.domain.value.TagGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByPlaceGroup(PlaceGroup placeGroup);

    List<Tag> findByPlaceGroupAndTagGroup(PlaceGroup placeGroup, TagGroup tagGroup);
}
