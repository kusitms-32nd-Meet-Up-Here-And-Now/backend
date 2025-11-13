package com.meetup.hereandnow.place.application.facade;

import com.meetup.hereandnow.core.infrastructure.value.SortType;
import com.meetup.hereandnow.core.util.SortUtils;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.infrastructure.repository.PinRepository;
import com.meetup.hereandnow.place.application.service.PlaceDtoConverter;
import com.meetup.hereandnow.place.application.service.PlaceFindService;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.response.PlaceCardResponseDto;
import com.meetup.hereandnow.place.dto.response.PlacePointResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceViewFacade {

    private final PlaceFindService placeFindService;
    private final PlaceDtoConverter placeDtoConverter;
    private final PinRepository pinRepository;

    @Transactional(readOnly = true)
    public List<PlacePointResponseDto> getAdPlaces(double lat, double lon) {
        List<Place> places = placeFindService.find2RandomNearbyPlaceIds(lat, lon);

        if (places.isEmpty()) {
            return Collections.emptyList();
        }

        List<Pin> pinList = pinRepository.find3PinsByPlaceIdsSorted(places.stream().map(Place::getId).toList());

        Map<Long, List<Pin>> pinsByPlaceId = pinList.stream()
                .collect(Collectors.groupingBy(pin -> pin.getPlace().getId()));

        return places.stream().map(place -> {
            List<Pin> pins = pinsByPlaceId.getOrDefault(place.getId(), Collections.emptyList());
            return placeDtoConverter.convert(place, pins);
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<PlaceCardResponseDto> getRecommendedPlaces(
            int page, int size, SortType sort, double lat, double lon
    ) {
        Pageable pageable = SortUtils.resolvePlaceSortNQ(page, size, sort);
        List<Place> places = placeFindService.findNearbyPlaces(lat, lon, pageable);
        return placeDtoConverter.convert(places);
    }
}
