package com.meetup.hereandnow.pin.infrastructure;

import com.meetup.hereandnow.pin.domain.entity.Pin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PinRepository extends JpaRepository<Pin, Long> {
}

