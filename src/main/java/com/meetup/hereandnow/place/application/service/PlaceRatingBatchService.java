package com.meetup.hereandnow.place.application.service;

import com.meetup.hereandnow.pin.infrastructure.repository.PinRepository;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.PlaceRatingDto;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceRatingBatchService {

    private final PinRepository pinRepository;
    private final PlaceRepository placeRepository;

    @Transactional
    public void process(List<Long> placeIds) {
        if (placeIds.isEmpty()) return;

        List<Place> places = placeRepository.findAllById(placeIds);
        List<PlaceRatingDto> stats = pinRepository.getPlaceRatingsByIds(placeIds);

        Map<Long, PlaceRatingDto> statMap = stats.stream()
                .collect(Collectors.toMap(PlaceRatingDto::placeId, Function.identity()));

        for (Place place : places) {
            PlaceRatingDto stat = statMap.get(place.getId());
            if (stat != null) {
                place.updateRating(
                        BigDecimal.valueOf(stat.placeRating()).setScale(1, RoundingMode.HALF_UP),
                        stat.pinCount()
                );
            } else {
                // Place에 딸린 핀이 0개가 된 경우
                place.updateRating(BigDecimal.ZERO, 0L);
            }
        }
    }
}
