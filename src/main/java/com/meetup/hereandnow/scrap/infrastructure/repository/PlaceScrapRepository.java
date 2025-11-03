package com.meetup.hereandnow.scrap.infrastructure.repository;

import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.scrap.domain.PlaceScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaceScrapRepository extends JpaRepository<PlaceScrap, Long> {

    Optional<PlaceScrap> findByMemberIdAndPlaceId(Long memberId, Long placeId);

    @Query(value = "SELECT ps FROM PlaceScrap ps JOIN FETCH ps.place p WHERE ps.member = :member ORDER BY ps.createdAt DESC",
            countQuery = "SELECT count(ps) FROM PlaceScrap ps WHERE ps.member = :member")
    Page<PlaceScrap> findByMemberWithPlace(@Param("member") Member member, Pageable pageable);
}
