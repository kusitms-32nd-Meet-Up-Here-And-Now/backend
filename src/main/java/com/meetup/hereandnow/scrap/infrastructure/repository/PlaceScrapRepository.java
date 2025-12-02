package com.meetup.hereandnow.scrap.infrastructure.repository;

import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.scrap.domain.PlaceScrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PlaceScrapRepository extends JpaRepository<PlaceScrap, Long> {

    Optional<PlaceScrap> findByMemberIdAndPlaceId(Long memberId, Long placeId);

    @Query("SELECT ps.place.id FROM PlaceScrap ps WHERE ps.member = :member AND ps.place IN :places")
    Set<Long> findScrappedPlaceIdsByMemberAndPlaces(
            @Param("member") Member member,
            @Param("places") List<Place> places
    );

    @Query(value = "SELECT ps FROM PlaceScrap ps JOIN FETCH ps.place p WHERE ps.member = :member",
            countQuery = "SELECT COUNT(ps) FROM  PlaceScrap ps WHERE ps.member = :member")
    Page<PlaceScrap> findScrapsByMemberWithSort(@Param("member") Member member, Pageable pageable);
}
