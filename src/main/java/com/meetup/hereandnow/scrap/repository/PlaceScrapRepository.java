package com.meetup.hereandnow.scrap.repository;

import com.meetup.hereandnow.scrap.domain.PlaceScrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceScrapRepository extends JpaRepository<PlaceScrap, Long> {

    Optional<PlaceScrap> findByMemberIdAndPlaceId(Long memberId, Long placeId);
}
