package com.meetup.hereandnow.pin.infrastructure.repository;

import com.meetup.hereandnow.pin.domain.entity.PinImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PinImageRepository extends JpaRepository<PinImage, Long> {
}
