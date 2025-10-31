package com.meetup.hereandnow.place.application.service;

import com.meetup.hereandnow.pin.domain.value.PinTagEnum;
import com.meetup.hereandnow.pin.infrastructure.repository.PinRepository;
import com.meetup.hereandnow.pin.infrastructure.repository.PinTagRepository;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.PlaceRatingDto;
import com.meetup.hereandnow.place.dto.PlaceTagDto;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceBatchServiceTest {

    @Mock
    private PinRepository pinRepository;
    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private PinTagRepository pinTagRepository;

    @InjectMocks
    private PlaceBatchService placeBatchService;

    private PinTagEnum mockTagEnum(String name) {
        PinTagEnum tag = mock(PinTagEnum.class);
        given(tag.getName()).willReturn(name);
        return tag;
    }

    @Test
    @DisplayName("process 호출 시 placeId 리스트가 비어있으면 즉시 빈 리스트 반환한다")
    void process_empty_list() {
        // given
        List<Long> placeIds = Collections.emptyList();

        // when
        placeBatchService.process(placeIds);

        // then
        then(placeRepository).should(never()).findAllById(any());
        then(pinRepository).should(never()).getPlaceRatingsByIds(any());
        then(pinTagRepository).should(never()).getPinTagsByPlaceIds(any());
    }

    @Test
    @DisplayName("process 호출 시 평점과 태그를 조회하여 Place 엔티티를 업데이트한다")
    void process_updates_places_ratings_and_tags() {
        // given
        List<Long> placeIds = List.of(1L, 2L, 3L);

        Place place1 = mock(Place.class);
        given(place1.getId()).willReturn(1L);
        Place place2 = mock(Place.class);
        given(place2.getId()).willReturn(2L);
        Place place3 = mock(Place.class);
        given(place3.getId()).willReturn(3L);
        List<Place> places = List.of(place1, place2, place3);
        given(placeRepository.findAllById(placeIds)).willReturn(places);

        PlaceRatingDto rating1 = new PlaceRatingDto(1L, 4.512, 10L);
        PlaceRatingDto rating3 = new PlaceRatingDto(3L, 3.2, 5L);
        List<PlaceRatingDto> ratings = List.of(rating1, rating3);
        given(pinRepository.getPlaceRatingsByIds(placeIds)).willReturn(ratings);

        PlaceTagDto tag1_1 = new PlaceTagDto(1L, mockTagEnum("TagA"), 1L);
        PlaceTagDto tag1_2 = new PlaceTagDto(1L, mockTagEnum("TagB"), 1L);
        PlaceTagDto tag2_1 = new PlaceTagDto(2L, mockTagEnum("TagC"), 1L);
        PlaceTagDto tag2_2 = new PlaceTagDto(2L, mockTagEnum("TagD"), 1L);
        PlaceTagDto tag2_3 = new PlaceTagDto(2L, mockTagEnum("TagE"), 1L);
        PlaceTagDto tag2_4 = new PlaceTagDto(2L, mock(PinTagEnum.class), 1L);
        List<PlaceTagDto> tags = List.of(tag1_1, tag1_2, tag2_1, tag2_2, tag2_3, tag2_4);
        given(pinTagRepository.getPinTagsByPlaceIds(placeIds)).willReturn(tags);

        // when
        placeBatchService.process(placeIds);

        // then
        then(place1).should(times(1)).updateRating(
                BigDecimal.valueOf(4.5).setScale(1, RoundingMode.HALF_UP),
                10L
        );
        then(place1).should(times(1)).updateTags(List.of("TagA", "TagB"));

        then(place2).should(times(1)).updateRating(BigDecimal.ZERO, 0L);
        then(place2).should(times(1)).updateTags(List.of("TagC", "TagD", "TagE"));

        then(place3).should(times(1)).updateRating(
                BigDecimal.valueOf(3.2).setScale(1, RoundingMode.HALF_UP),
                5L
        );
        then(place3).should(times(1)).updateTags(Collections.emptyList());
    }

    @Test
    @DisplayName("process 호출 시 평점과 태그가 모두 없는 경우에는 0점과 빈 리스트로 처리한다")
    void process_no_ratings_and_no_tags() {
        // given
        List<Long> placeIds = List.of(1L);
        Place place1 = mock(Place.class);
        given(place1.getId()).willReturn(1L);
        List<Place> places = List.of(place1);

        given(placeRepository.findAllById(placeIds)).willReturn(places);
        given(pinRepository.getPlaceRatingsByIds(placeIds)).willReturn(Collections.emptyList());
        given(pinTagRepository.getPinTagsByPlaceIds(placeIds)).willReturn(Collections.emptyList());

        // when
        placeBatchService.process(placeIds);

        // then
        then(place1).should(times(1)).updateRating(BigDecimal.ZERO, 0L);
        then(place1).should(times(1)).updateTags(Collections.emptyList());
    }
}

