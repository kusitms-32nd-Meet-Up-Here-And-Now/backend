package com.meetup.hereandnow.scrap.application.service;

import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import com.meetup.hereandnow.scrap.domain.PlaceScrap;
import com.meetup.hereandnow.scrap.dto.response.ScrapResponseDto;
import com.meetup.hereandnow.scrap.exception.ScrapErrorCode;
import com.meetup.hereandnow.scrap.repository.PlaceScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaceScrapService {

    private final PlaceScrapRepository placeScrapRepository;
    private final PlaceRepository placeRepository;

    public Optional<PlaceScrap> findOptional(Member member, Long placeId) {
        return placeScrapRepository.findByMemberIdAndPlaceId(member.getId(), placeId);
    }

    public ScrapResponseDto scrap(Member member, Long placeId) {
        Optional<Place> place = placeRepository.findById(placeId);
        if (place.isPresent()) {
            Optional<PlaceScrap> existingScrap =
                    placeScrapRepository.findByMemberIdAndPlaceId(member.getId(), placeId);
            if (existingScrap.isPresent()) {
                return ScrapResponseDto.from(existingScrap.get());
            }

            PlaceScrap scrap = PlaceScrap.builder()
                    .member(member)
                    .place(place.get())
                    .build();
            placeScrapRepository.save(scrap);
            return ScrapResponseDto.from(scrap);
        } else {
            throw ScrapErrorCode.PLACE_NOT_FOUND.toException();
        }
    }

    public ScrapResponseDto deleteScrap(PlaceScrap placeScrap) {
        placeScrapRepository.delete(placeScrap);
        return ScrapResponseDto.from();
    }
}
