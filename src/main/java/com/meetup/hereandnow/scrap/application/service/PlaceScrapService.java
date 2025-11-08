package com.meetup.hereandnow.scrap.application.service;

import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import com.meetup.hereandnow.scrap.domain.PlaceScrap;
import com.meetup.hereandnow.scrap.dto.response.ScrapResponseDto;
import com.meetup.hereandnow.scrap.exception.ScrapErrorCode;
import com.meetup.hereandnow.scrap.infrastructure.repository.PlaceScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaceScrapService {

    private final PlaceScrapRepository placeScrapRepository;
    private final PlaceRepository placeRepository;

    /**
     * PlaceScrap을 생성 또는 삭제합니다.
     */
    public ScrapResponseDto toggleScrapPlace(Member member, Long placeId) {

        Place place = placeRepository.findByIdWithLock(placeId)
                .orElseThrow(ScrapErrorCode.PLACE_NOT_FOUND::toException);

        Optional<PlaceScrap> optionalPlaceScrap =
                placeScrapRepository.findByMemberIdAndPlaceId(member.getId(), placeId);

        if (optionalPlaceScrap.isEmpty()) {
            PlaceScrap scrap = PlaceScrap.builder()
                    .member(member)
                    .place(place)
                    .build();
            place.incrementScrapCount();
            placeScrapRepository.save(scrap);
            return ScrapResponseDto.from(scrap);

        } else {
            place.decrementScrapCount();
            placeScrapRepository.delete(optionalPlaceScrap.get());
            return ScrapResponseDto.from();
        }
    }

    /**
     * member가 저장한 PlaceScrap을 페이징해 반환합니다.
     */
    public Page<PlaceScrap> getScrapsByMember(Member member, Pageable pageable) {
        return placeScrapRepository.findScrapsByMemberWithSort(member, pageable);
    }

    /**
     * 전달된 정렬 값을 pageable로 알맞게 처리합니다. 기본값은 최신순입니다.
     */
    public Pageable resolveSort(int page, int size, String sort) {
        String resolvedSortBy = switch (sort.toLowerCase()) {
            case "scraps" -> "place.scrapCount";
            case "reviews" -> "place.pinCount";
            default -> "createdAt";
        };
        return PageRequest.of(
                page, size,
                Sort.by(Sort.Direction.DESC, resolvedSortBy)
        );
    }
}
