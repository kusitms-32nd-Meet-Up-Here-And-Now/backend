package com.meetup.hereandnow.pin.infrastructure.repository;

import com.meetup.hereandnow.pin.domain.entity.CouplePinRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouplePinRecordRepository extends JpaRepository<CouplePinRecord, Long> {
}
