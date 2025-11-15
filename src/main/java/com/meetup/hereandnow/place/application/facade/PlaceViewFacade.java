package com.meetup.hereandnow.place.application.facade;

import com.meetup.hereandnow.core.infrastructure.value.SortType;
import com.meetup.hereandnow.core.util.SortUtils;
import com.meetup.hereandnow.course.dto.response.SearchFilterDto;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.infrastructure.repository.PinRepository;
import com.meetup.hereandnow.place.application.service.PlaceDtoConverter;
import com.meetup.hereandnow.place.application.service.PlaceFindService;
import com.meetup.hereandnow.place.application.service.PlaceSearchService;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.response.PlaceCardMarkerResponseDto;
import com.meetup.hereandnow.place.dto.response.PlaceCardResponseDto;
import com.meetup.hereandnow.place.dto.response.PlacePointResponseDto;
import com.meetup.hereandnow.place.dto.response.PlaceSearchResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final PlaceSearchService placeSearchService;

    /*
    홈 화면에서의 랜덤(광고) 장소를 마커+타원형 dto 형태로 반환합니다.
     */
    @Transactional(readOnly = true)
    public List<PlacePointResponseDto> getAdPlaces(double lat, double lon) {
        List<Place> places = placeFindService.find2RandomNearbyPlaces(lat, lon);

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

    /*
    둘러보기 화면에서의 랜덤(광고) 장소를 카드+마커 형태로 반환합니다.
     */
    @Transactional(readOnly = true)
    public List<PlaceCardMarkerResponseDto> getAdPlacesWithMarker(double lat, double lon) {
        List<Place> places = placeFindService.find2RandomNearbyPlaces(lat, lon);
        if (places.isEmpty()) {
            return Collections.emptyList();
        }
        return placeDtoConverter.convertWithMarker(places);
    }

    /*
    홈 화면에서의 정렬별 추천 장소 리스트를 반환합니다.
     */
    @Transactional(readOnly = true)
    public List<PlaceCardResponseDto> getRecommendedPlaces(
            int page, int size, SortType sort, double lat, double lon
    ) {
        Pageable pageable = SortUtils.resolvePlaceSortNQ(page, size, sort);
        List<Place> places = placeFindService.findNearbyPlaces(lat, lon, pageable);
        return placeDtoConverter.convert(places);
    }

    /*
    둘러보기 화면에서의 장소 검색 결과 및 적용된 필터를 반환합니다.
     */
    @Transactional(readOnly = true)
    public PlaceSearchResponseDto getFilteredPlaces(
            int page, int size, Integer rating, List<String> keyword, LocalDate startDate, LocalDate endDate,
            String with, String region, List<String> placeCode, List<String> tag
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "pinCount"));
        Page<Place> placePage = placeSearchService.searchPlaces(
                rating, keyword, startDate, endDate, with, region, placeCode, tag, pageable
        );
        SearchFilterDto searchFilterDto = new SearchFilterDto(
                rating, keyword, startDate, endDate, with, region, placeCode, tag
        );
        List<PlaceCardMarkerResponseDto> filteredPlaces = placePage.hasContent() ?
                placeDtoConverter.convertWithMarker(placePage.getContent())
                : Collections.emptyList();
        return new PlaceSearchResponseDto(searchFilterDto, filteredPlaces);
    }
}
