package com.meetup.hereandnow.place.application.service;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import com.meetup.hereandnow.pin.infrastructure.repository.PinImageRepository;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.PlaceCardResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceCardDtoConverter {

    private final PinImageRepository pinImageRepository;
    private final ObjectStorageService objectStorageService;

    public List<PlaceCardResponseDto> convert(List<Place> places) {
        if (places.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, String> imageUrlMap = getImageUrlsByPlaceIdsMap(places);
        return places.stream()
                .map(place -> PlaceCardResponseDto.from(place, imageUrlMap.get(place.getId())))
                .toList();
    }

    /**
     * Place 리스트의 각 장소마다 제일 최신 Pin의 이미지를 가져온 후 전체 url로 빌드하고,
     * placeId로 해당 최신 이미지 url을 조회할 수 있는 map을 반환합니다.
     */
    private Map<Long, String> getImageUrlsByPlaceIdsMap(List<Place> places) {
        List<Long> placeIds = places.stream().map(Place::getId).toList();
        List<PinImage> placeImages = pinImageRepository.findRecentImagesByPlaceIds(placeIds);
        return placeImages.stream().collect(Collectors.toMap(
                img -> img.getPin().getPlace().getId(),
                img -> objectStorageService.buildImageUrl(img.getImageUrl())
        ));
    }
}
