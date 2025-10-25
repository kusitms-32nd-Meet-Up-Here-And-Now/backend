package com.meetup.hereandnow.pin.infrastructure.repository;

import com.meetup.hereandnow.pin.domain.entity.PinTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PinTagRepository extends JpaRepository<PinTag, Long> {
}

