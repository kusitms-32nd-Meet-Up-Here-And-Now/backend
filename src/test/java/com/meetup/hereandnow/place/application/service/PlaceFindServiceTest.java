package com.meetup.hereandnow.place.application.service;

import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceFindServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private PlaceFindService placeFindService;

    private GeometryFactory geometryFactory = new GeometryFactory();

    private static final String TEST_PLACE_NAME = "장소 이름";
    private static final String TEST_PLACE_STREET_ADDRESS = "장소 도로명 주소";
    private static final String TEST_PLACE_NUMBER_ADDRESS = "장소 지번 주소";
    private static final double TEST_LAT = 37.1;
    private static final double TEST_LON = 127.1;

    private Place mockPlace1;
    private Place mockPlace2;

    @BeforeEach
    void setUp() {
        mockPlace1 = mock(Place.class);
        mockPlace2 = mock(Place.class);
    }

    @Test
    @DisplayName("Place 객체 1개가 정상적으로 반환된다.")
    void success_find_place() {
        // given
        Coordinate coord = new Coordinate(TEST_LON, TEST_LAT);
        Point point = geometryFactory.createPoint(coord);

        Place expected = Place.builder()
                .placeName(TEST_PLACE_NAME)
                .placeStreetNameAddress(TEST_PLACE_STREET_ADDRESS)
                .placeNumberAddress(TEST_PLACE_NUMBER_ADDRESS)
                .location(point)
                .build();

        when(placeRepository.findByNameAndCoordinates(TEST_PLACE_NAME, TEST_LAT, TEST_LON)).thenReturn(Optional.of(expected));

        // when
        Optional<Place> result = placeFindService.findByNameAndCoordinates(TEST_PLACE_NAME, TEST_LAT, TEST_LON);

        // then
        assertThat(result).isPresent();
        assertThat(result).contains(expected);
    }

    @Test
    @DisplayName("findNearbyPlaces: 주변 장소 Page를 List로 변환하여 반환한다")
    void find_nearby_places() {

        // given
        Pageable mockPageable = mock(Pageable.class);
        List<Place> places = List.of(mockPlace1, mockPlace2);
        Page<Place> mockPage = new PageImpl<>(places, mockPageable, places.size());

        given(placeRepository.findPlacesByLocation(TEST_LAT, TEST_LON, mockPageable)).willReturn(mockPage);

        // when
        List<Place> result = placeFindService.findNearbyPlaces(TEST_LAT, TEST_LON, mockPageable);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(mockPlace1, mockPlace2);
        verify(placeRepository).findPlacesByLocation(TEST_LAT, TEST_LON, mockPageable);
    }

    @Test
    @DisplayName("findNearbyPlaces: 주변 장소가 없으면 빈 리스트를 반환한다")
    void find_nearby_places_empty_list() {

        // given
        Pageable mockPageable = mock(Pageable.class);
        Page<Place> emptyPage = new PageImpl<>(Collections.emptyList(), mockPageable, 0);

        given(placeRepository.findPlacesByLocation(TEST_LAT, TEST_LON, mockPageable)).willReturn(emptyPage);

        // when
        List<Place> result = placeFindService.findNearbyPlaces(TEST_LAT, TEST_LON, mockPageable);

        // then
        assertThat(result).isEmpty();
        verify(placeRepository).findPlacesByLocation(TEST_LAT, TEST_LON, mockPageable);
    }

    @Test
    @DisplayName("find2RandomNearbyPlaceIds: 주변 장소 ID가 없으면 빈 리스트를 반환한다")
    void find_2_random_nearby_place_ids_empty_list() {

        // given
        given(placeRepository.findNearbyPlaceIds(TEST_LAT, TEST_LON)).willReturn(Collections.emptyList());

        // when
        List<Place> result = placeFindService.find2RandomNearbyPlaces(TEST_LAT, TEST_LON);

        // then
        assertThat(result).isEmpty();
        verify(placeRepository).findNearbyPlaceIds(TEST_LAT, TEST_LON);
        verify(placeRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("find2RandomNearbyPlaceIds: 주변 장소가 2개 이상이면 2개의 Place 리스트를 반환한다")
    void find_2_random_nearby_place_when_many_ids_found() {

        // given
        List<Long> originalIds = new ArrayList<>(List.of(1L, 2L, 3L));
        List<Place> expectedPlaces = List.of(mockPlace1, mockPlace2);

        ArgumentCaptor<List<Long>> idListCaptor = ArgumentCaptor.forClass(List.class);

        given(placeRepository.findNearbyPlaceIds(TEST_LAT, TEST_LON)).willReturn(originalIds);
        given(placeRepository.findAllById(idListCaptor.capture())).willReturn(expectedPlaces);

        // when
        List<Place> result = placeFindService.find2RandomNearbyPlaces(TEST_LAT, TEST_LON);

        // then
        assertThat(result).isEqualTo(expectedPlaces);

        verify(placeRepository).findNearbyPlaceIds(TEST_LAT, TEST_LON);
        verify(placeRepository).findAllById(anyList());

        List<Long> capturedIds = idListCaptor.getValue();
        assertThat(capturedIds).hasSize(2);
        assertThat(List.of(1L, 2L, 3L)).containsAll(capturedIds);
    }

    @Test
    @DisplayName("find2RandomNearbyPlaceIds: 주변 장소가 1개면 1개의 Place 리스트를 반환한다")
    void find_2_random_nearby_place_ids_when_one_id_found() {

        // given
        List<Long> originalIds = List.of(1L);
        List<Place> expectedPlaces = List.of(mockPlace1);
        List<Long> expectedFinalIds = List.of(1L);

        given(placeRepository.findNearbyPlaceIds(TEST_LAT, TEST_LON)).willReturn(originalIds);
        given(placeRepository.findAllById(eq(expectedFinalIds))).willReturn(expectedPlaces);

        // when
        List<Place> result = placeFindService.find2RandomNearbyPlaces(TEST_LAT, TEST_LON);

        // then
        assertThat(result).isEqualTo(expectedPlaces);
        verify(placeRepository).findNearbyPlaceIds(TEST_LAT, TEST_LON);
        verify(placeRepository).findAllById(eq(expectedFinalIds));
    }
}