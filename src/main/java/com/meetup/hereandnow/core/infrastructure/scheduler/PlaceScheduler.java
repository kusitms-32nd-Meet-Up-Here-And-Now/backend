package com.meetup.hereandnow.core.infrastructure.scheduler;

import com.meetup.hereandnow.place.application.service.PlaceBatchService;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceScheduler {

    private final PlaceRepository placeRepository;
    private final PlaceBatchService placeBatchService;

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
