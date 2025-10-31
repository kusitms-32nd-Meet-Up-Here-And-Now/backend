package com.meetup.hereandnow.archive.application.service;

import com.meetup.hereandnow.archive.dto.response.PlaceCardDto;
import com.meetup.hereandnow.pin.dto.PlaceIdWithImage;
import com.meetup.hereandnow.pin.infrastructure.repository.PinImageRepository;
import com.meetup.hereandnow.place.domain.Place;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceCardDtoConverterService {

    private final PinImageRepository pinImageRepository;

    @Transactional(readOnly = true)
    public List<PlaceCardDto> toPlaceCardDtoList(List<Place> places) {
        if (places == null || places.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> placeIds = places.stream().map(Place::getId).toList();
        Map<Long, List<String>> imageUrlMap = getImageUrlMap(placeIds);

        return places.stream()
                .map(place -> toDto(
                        place,
                        imageUrlMap.getOrDefault(place.getId(), Collections.emptyList())
                ))
                .toList();
    }

    private Map<Long, List<String>> getImageUrlMap(List<Long> placeIds) {
        List<PlaceIdWithImage> dtoList = pinImageRepository.findImageUrlsByPlaceIds(placeIds);
        return dtoList.stream().collect(Collectors.groupingBy(
                PlaceIdWithImage::getPlaceId,
                Collectors.mapping(
                        PlaceIdWithImage::getImageUrl,
                        Collectors.toList()
                )
        ));
    }

    private PlaceCardDto toDto(Place place, List<String> imageUrls) {
        double placeRating = place.getPlaceRating() == null ? 0L : place.getPlaceRating().doubleValue();
        List<String> placeTags = place.getPlaceTags() == null ? Collections.emptyList() : place.getPlaceTags();
        return new PlaceCardDto(
                place.getId(),
                place.getPlaceName(),
                place.getPlaceAddress(),
                placeRating,
                placeTags,
                imageUrls
        );
    }
}
