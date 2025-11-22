package com.meetup.hereandnow.tag.infrastructure.repository;

import com.meetup.hereandnow.tag.domain.entity.PlaceGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceGroupRepository extends JpaRepository<PlaceGroup, Long> {

    Optional<PlaceGroup> findByCode(String code);
}
