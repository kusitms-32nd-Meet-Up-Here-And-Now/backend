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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceViewFacadeTest {

    @Mock
    private PlaceFindService placeFindService;
    @Mock
    private PlaceDtoConverter placeDtoConverter;
    @Mock
    private PinRepository pinRepository;

    @InjectMocks
    private PlaceViewFacade placeViewFacade;

    private MockedStatic<SortUtils> mockedSortUtils;
    private static final double TEST_LAT = 37.5;
    private static final double TEST_LON = 127.0;

    @BeforeEach
    void setUp() {
        mockedSortUtils = mockStatic(SortUtils.class);
    }

    @AfterEach
    void tearDown() {
        mockedSortUtils.close();
    }

    @Test
    @DisplayName("getAdPlaces: 장소와 핀을 그룹핑하여 DTO 리스트로 변환한다")
    void get_ad_places() {

        // given
        Place mockPlace1 = mock(Place.class);
        Place mockPlace2 = mock(Place.class);
        List<Place> places = List.of(mockPlace1, mockPlace2);

        Pin mockPin1 = mock(Pin.class); // place1의 핀
        Pin mockPin2 = mock(Pin.class); // place1의 핀
        Pin mockPin3 = mock(Pin.class); // place2의 핀
        List<Pin> pinList = List.of(mockPin1, mockPin2, mockPin3);

        PlacePointResponseDto mockDto1 = mock(PlacePointResponseDto.class);
        PlacePointResponseDto mockDto2 = mock(PlacePointResponseDto.class);

        given(placeFindService.find2RandomNearbyPlaceIds(TEST_LAT, TEST_LON)).willReturn(places);

        given(mockPlace1.getId()).willReturn(1L);
        given(mockPlace2.getId()).willReturn(2L);
        List<Long> placeIds = List.of(1L, 2L);

        given(pinRepository.find3PinsByPlaceIdsSorted(placeIds)).willReturn(pinList);

        Place placeFromPin1 = mock(Place.class);
        Place placeFromPin2 = mock(Place.class);
        given(placeFromPin1.getId()).willReturn(1L);
        given(placeFromPin2.getId()).willReturn(2L);

        given(mockPin1.getPlace()).willReturn(placeFromPin1);
        given(mockPin2.getPlace()).willReturn(placeFromPin1);
        given(mockPin3.getPlace()).willReturn(placeFromPin2);

        List<Pin> pinsForPlace1 = List.of(mockPin1, mockPin2);
        List<Pin> pinsForPlace2 = List.of(mockPin3);

        given(placeDtoConverter.convert(mockPlace1, pinsForPlace1)).willReturn(mockDto1);
        given(placeDtoConverter.convert(mockPlace2, pinsForPlace2)).willReturn(mockDto2);

        // when
        List<PlacePointResponseDto> result = placeViewFacade.getAdPlaces(TEST_LAT, TEST_LON);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(mockDto1, mockDto2);

        verify(placeFindService).find2RandomNearbyPlaceIds(TEST_LAT, TEST_LON);
        verify(pinRepository).find3PinsByPlaceIdsSorted(placeIds);
        verify(placeDtoConverter).convert(mockPlace1, pinsForPlace1);
        verify(placeDtoConverter).convert(mockPlace2, pinsForPlace2);
    }

    @Test
    @DisplayName("getAdPlaces: 일부 장소에 핀이 없으면 빈 리스트로 DTO를 생성한다")
    void get_ad_places_empty_pins() {

        // given
        Place mockPlace1 = mock(Place.class);
        Place mockPlace2 = mock(Place.class);
        List<Place> places = List.of(mockPlace1, mockPlace2);

        given(mockPlace1.getId()).willReturn(1L);
        given(mockPlace2.getId()).willReturn(2L);
        List<Long> placeIds = List.of(1L, 2L);

        // Place1의 핀
        Pin mockPin1 = mock(Pin.class);
        List<Pin> pinList = List.of(mockPin1);

        PlacePointResponseDto mockDto1 = mock(PlacePointResponseDto.class);
        PlacePointResponseDto mockDto2 = mock(PlacePointResponseDto.class);

        given(placeFindService.find2RandomNearbyPlaceIds(TEST_LAT, TEST_LON)).willReturn(places);
        given(pinRepository.find3PinsByPlaceIdsSorted(placeIds)).willReturn(pinList);

        Place placeFromPin1 = mock(Place.class);
        given(placeFromPin1.getId()).willReturn(1L);
        given(mockPin1.getPlace()).willReturn(placeFromPin1);

        List<Pin> pinsForPlace1 = List.of(mockPin1);
        List<Pin> pinsForPlace2 = Collections.emptyList();

        given(placeDtoConverter.convert(mockPlace1, pinsForPlace1)).willReturn(mockDto1);
        given(placeDtoConverter.convert(mockPlace2, pinsForPlace2)).willReturn(mockDto2);

        // when
        List<PlacePointResponseDto> result = placeViewFacade.getAdPlaces(TEST_LAT, TEST_LON);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(mockDto1, mockDto2);

        verify(placeDtoConverter).convert(mockPlace1, pinsForPlace1);
        verify(placeDtoConverter).convert(mockPlace2, pinsForPlace2);
    }

    @Test
    @DisplayName("getAdPlaces: 장소가 없으면 빈 리스트를 반환한다")
    void get_ad_places_returns_when_no_places_found() {

        // given
        given(placeFindService.find2RandomNearbyPlaceIds(TEST_LAT, TEST_LON)).willReturn(Collections.emptyList());

        // when
        List<PlacePointResponseDto> result = placeViewFacade.getAdPlaces(TEST_LAT, TEST_LON);

        // then
        assertThat(result).isEmpty();

        verify(placeFindService).find2RandomNearbyPlaceIds(TEST_LAT, TEST_LON);
        verify(pinRepository, never()).find3PinsByPlaceIdsSorted(Collections.emptyList());
        verify(placeDtoConverter, never()).convert(any(Place.class), anyList());
    }

    @Test
    @DisplayName("getRecommendedPlaces: 정렬된 주변 장소를 DTO 리스트로 변환한다")
    void get_recommended_places() {

        // given
        int page = 0;
        int size = 10;
        SortType sort = SortType.RECENT;

        Pageable mockPageable = mock(Pageable.class);
        List<Place> places = List.of(mock(Place.class), mock(Place.class));
        List<PlaceCardResponseDto> dtos = List.of(mock(PlaceCardResponseDto.class), mock(PlaceCardResponseDto.class));

        mockedSortUtils.when(() -> SortUtils.resolvePlaceSortNQ(page, size, sort)).thenReturn(mockPageable);
        given(placeFindService.findNearbyPlaces(TEST_LAT, TEST_LON, mockPageable)).willReturn(places);
        given(placeDtoConverter.convert(places)).willReturn(dtos);

        // when
        List<PlaceCardResponseDto> result = placeViewFacade.getRecommendedPlaces(page, size, sort, TEST_LAT, TEST_LON);

        // then
        assertThat(result).isEqualTo(dtos);
        mockedSortUtils.verify(() -> SortUtils.resolvePlaceSortNQ(page, size, sort));
        verify(placeFindService).findNearbyPlaces(TEST_LAT, TEST_LON, mockPageable);
        verify(placeDtoConverter).convert(places);
    }

    @Test
    @DisplayName("getRecommendedPlaces: 장소가 없으면 빈 리스트를 반환한다")
    void get_recommended_places_when_no_places_found() {

        // given
        int page = 0;
        int size = 10;
        SortType sort = SortType.RECENT;

        Pageable mockPageable = mock(Pageable.class);
        List<Place> emptyPlaces = Collections.emptyList();
        List<PlaceCardResponseDto> emptyDtos = Collections.emptyList();


        mockedSortUtils.when(() -> SortUtils.resolvePlaceSortNQ(page, size, sort)).thenReturn(mockPageable);
        given(placeFindService.findNearbyPlaces(TEST_LAT, TEST_LON, mockPageable)).willReturn(emptyPlaces);
        given(placeDtoConverter.convert(emptyPlaces)).willReturn(emptyDtos);

        // when
        List<PlaceCardResponseDto> result = placeViewFacade.getRecommendedPlaces(page, size, sort, TEST_LAT, TEST_LON);

        // then
        assertThat(result).isEmpty();

        verify(placeFindService).findNearbyPlaces(TEST_LAT, TEST_LON, mockPageable);
        verify(placeDtoConverter).convert(emptyPlaces);
    }
}