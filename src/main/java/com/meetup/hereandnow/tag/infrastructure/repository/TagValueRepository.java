package com.meetup.hereandnow.tag.infrastructure.repository;

import com.meetup.hereandnow.tag.domain.entity.TagValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagValueRepository extends JpaRepository<TagValue, Long> {
}
