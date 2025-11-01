package com.meetup.hereandnow.archive.application.service;

import com.meetup.hereandnow.archive.dto.response.PlaceCardDto;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.place.application.service.PlaceBatchService;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import com.meetup.hereandnow.scrap.domain.PlaceScrap;
import com.meetup.hereandnow.scrap.repository.PlaceScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArchivePlaceService {

    private final PlaceRepository placeRepository;
    private final PlaceScrapRepository placeScrapRepository;
    private final PlaceBatchService placeBatchService;
    private final PlaceCardDtoConverterService converterService;

    public List<PlaceCardDto> getMyScrappedPlaces(Member member, PageRequest pageRequest) {
        Page<PlaceScrap> scrapPage = placeScrapRepository.findByMemberWithPlace(member, pageRequest);
        List<Place> places = scrapPage.stream()
                .map(PlaceScrap::getPlace)
                .toList();
        if (places.isEmpty()) {
            return Collections.emptyList();
        }
        return converterService.toPlaceCardDtoList(places);
    }

    @Scheduled(cron = "0 37 4 * * *")
    @Transactional
    public void updatePlaceRatingAndTags() {
        Page<Long> placeIdPage;
        int pageNumber = 0;
        do {
            placeIdPage = placeRepository.findAllIds(PageRequest.of(pageNumber, 1000));
            if (placeIdPage.hasContent()) {
                placeBatchService.process(placeIdPage.getContent());
            }
            pageNumber++;
        } while (placeIdPage.hasNext());
    }
}
