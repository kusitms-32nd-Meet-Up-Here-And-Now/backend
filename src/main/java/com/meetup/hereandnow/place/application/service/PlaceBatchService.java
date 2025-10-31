package com.meetup.hereandnow.place.application.service;

import com.meetup.hereandnow.pin.infrastructure.repository.PinRepository;
import com.meetup.hereandnow.pin.infrastructure.repository.PinTagRepository;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.PlaceRatingDto;
import com.meetup.hereandnow.place.dto.PlaceTagDto;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceBatchService {

    private final PinRepository pinRepository;
    private final PlaceRepository placeRepository;
    private final PinTagRepository pinTagRepository;

    public void process(List<Long> placeIds) {
        if (placeIds.isEmpty()) return;

        List<Place> places = placeRepository.findAllById(placeIds);
        Map<Long, List<String>> topTagMap = getTopTagMap(placeIds);
        Map<Long, PlaceRatingDto> ratingMap = getRatingStatMap(placeIds);

        for (Place place : places) {
            PlaceRatingDto stat = ratingMap.get(place.getId());
            if (stat != null) {
                place.updateRating(
                        BigDecimal.valueOf(stat.placeRating()).setScale(1, RoundingMode.HALF_UP),
                        stat.pinCount()
                );
            } else {
                // Place에 딸린 핀이 0개가 된 경우
                place.updateRating(BigDecimal.ZERO, 0L);
            }
            List<String> topTags = topTagMap.getOrDefault(place.getId(), Collections.emptyList());
            place.updateTags(topTags);
        }
    }

    private Map<Long, PlaceRatingDto> getRatingStatMap(List<Long> placeIds) {
        List<PlaceRatingDto> stats = pinRepository.getPlaceRatingsByIds(placeIds);
        return stats.stream()
                .collect(Collectors.toMap(PlaceRatingDto::placeId, Function.identity()));
    }

    private Map<Long, List<String>> getTopTagMap(List<Long> placeIds) {
        List<PlaceTagDto> stats = pinTagRepository.getPinTagsByPlaceIds(placeIds);

        // placeId별로 태그 리스트 만드는 map
        Map<Long, List<PlaceTagDto>> groupedByPlace = stats.stream()
                .collect(Collectors.groupingBy(
                        PlaceTagDto::placeId,
                        Collectors.toList()
                ));

        Map<Long, List<String>> finalTopTagsMap = new HashMap<>();

        // placeId별로 태그 리스트 중에서 최대 3개만 골라 한글 태그명 리스트 매핑
        for (Map.Entry<Long, List<PlaceTagDto>> entry : groupedByPlace.entrySet()) {
            Long placeId = entry.getKey();
            List<String> topTags = entry.getValue().stream()
                    .limit(3)
                    .map(stat -> stat.tagEnum().getName())
                    .toList();
            finalTopTagsMap.put(placeId, topTags);
        }
        return finalTopTagsMap;
    }
}
