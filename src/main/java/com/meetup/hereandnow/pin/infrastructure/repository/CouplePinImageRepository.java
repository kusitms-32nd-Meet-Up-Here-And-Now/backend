package com.meetup.hereandnow.pin.infrastructure.repository;

import com.meetup.hereandnow.pin.domain.entity.CouplePinImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouplePinImageRepository extends JpaRepository<CouplePinImage, Long> {
}
