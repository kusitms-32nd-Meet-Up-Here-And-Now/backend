package com.meetup.hereandnow.place.application.service;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import com.meetup.hereandnow.pin.infrastructure.repository.PinImageRepository;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.response.PlaceCardMarkerResponseDto;
import com.meetup.hereandnow.place.dto.response.PlaceCardResponseDto;
import com.meetup.hereandnow.place.dto.response.PlaceMarkerResponseDto;
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

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceDtoConverterTest {

    @Mock
    private PinImageRepository pinImageRepository;

    @Mock
    private ObjectStorageService objectStorageService;

    @InjectMocks
    private PlaceDtoConverter placeDtoConverter;

    private MockedStatic<PlaceCardResponseDto> placeCardDto;
    private MockedStatic<PlacePointResponseDto> placePointDto;
    private MockedStatic<PlaceMarkerResponseDto> placeMarkerDto;
    private MockedStatic<PlaceCardMarkerResponseDto> placeCardMarkerDto;

    @BeforeEach
    void setUp() {
        placeCardDto = mockStatic(PlaceCardResponseDto.class);
        placePointDto = mockStatic(PlacePointResponseDto.class);
        placeMarkerDto = mockStatic(PlaceMarkerResponseDto.class);
        placeCardMarkerDto = mockStatic(PlaceCardMarkerResponseDto.class);
    }

    @AfterEach
    void tearDown() {
        placeCardDto.close();
        placePointDto.close();
        placeMarkerDto.close();
        placeCardMarkerDto.close();
    }

    @Test
    @DisplayName("빈 리스트가 주어지면 빈 리스트를 반환한다")
    void convert_with_empty_list_returns_empty_list() {

        // given
        List<Place> emptyList = Collections.emptyList();

        // when
        List<PlaceCardResponseDto> result = placeDtoConverter.convert(emptyList);

        // then
        assertThat(result).isEmpty();

        verify(pinImageRepository, never()).findRecentImagesByPlaceIds(any());
        verify(objectStorageService, never()).buildImageUrl(anyString());
        placeCardDto.verify(() -> PlaceCardResponseDto.from(any(), anyString()), never());
    }

    @Test
    @DisplayName("장소 목록과 이미지가 주어지면 DTO 리스트로 올바르게 변환한다")
    void convert_with_places_and_images_maps_correctly() {

        // given
        Place mockPlace1 = mock(Place.class);
        Place mockPlace2 = mock(Place.class);
        given(mockPlace1.getId()).willReturn(1L);
        given(mockPlace2.getId()).willReturn(2L);
        List<Place> places = List.of(mockPlace1, mockPlace2);
        List<Long> placeIds = List.of(1L, 2L);

        PinImage img1 = mock(PinImage.class);
        PinImage img2 = mock(PinImage.class);
        Pin pin1 = mock(Pin.class);
        Pin pin2 = mock(Pin.class);
        Place placeFromPin1 = mock(Place.class);
        Place placeFromPin2 = mock(Place.class);

        given(img1.getPin()).willReturn(pin1);
        given(pin1.getPlace()).willReturn(placeFromPin1);
        given(placeFromPin1.getId()).willReturn(1L);
        given(img1.getImageUrl()).willReturn("img1.jpg");

        given(img2.getPin()).willReturn(pin2);
        given(pin2.getPlace()).willReturn(placeFromPin2);
        given(placeFromPin2.getId()).willReturn(2L);
        given(img2.getImageUrl()).willReturn("img2.jpg");

        given(pinImageRepository.findRecentImagesByPlaceIds(placeIds)).willReturn(List.of(img1, img2));
        given(objectStorageService.buildImageUrl("img1.jpg")).willReturn("http://img1.jpg");
        given(objectStorageService.buildImageUrl("img2.jpg")).willReturn("http://img2.jpg");

        PlaceCardResponseDto dto1 = mock(PlaceCardResponseDto.class);
        PlaceCardResponseDto dto2 = mock(PlaceCardResponseDto.class);
        placeCardDto.when(() -> PlaceCardResponseDto.from(mockPlace1, "http://img1.jpg")).thenReturn(dto1);
        placeCardDto.when(() -> PlaceCardResponseDto.from(mockPlace2, "http://img2.jpg")).thenReturn(dto2);

        // when
        List<PlaceCardResponseDto> result = placeDtoConverter.convert(places);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);

        verify(pinImageRepository).findRecentImagesByPlaceIds(placeIds);
        verify(objectStorageService).buildImageUrl("img1.jpg");
        verify(objectStorageService).buildImageUrl("img2.jpg");
        placeCardDto.verify(() -> PlaceCardResponseDto.from(mockPlace1, "http://img1.jpg"));
        placeCardDto.verify(() -> PlaceCardResponseDto.from(mockPlace2, "http://img2.jpg"));
    }

    @Test
    @DisplayName("첫 3개 핀의 ID가 가장 낮은 이미지만 DTO로 변환한다")
    void convert_processes_limit_3_pins() {

        // given
        Place mockPlace = mock(Place.class);
        Pin pin1 = mock(Pin.class);
        Pin pin2 = mock(Pin.class);
        Pin pin3 = mock(Pin.class);
        Pin pin4 = mock(Pin.class); // 무시돼야 하는 핀 

        PinImage img1a = mock(PinImage.class);
        given(img1a.getId()).willReturn(10L);
        PinImage img1b = mock(PinImage.class);
        given(img1b.getId()).willReturn(5L); // 선정될 이미지
        given(img1b.getImageUrl()).willReturn("img1b.jpg");
        given(pin1.getPinImages()).willReturn(List.of(img1a, img1b));

        PinImage img2a = mock(PinImage.class);
        given(img2a.getId()).willReturn(2L); // 선정될 이미지
        PinImage img2b = mock(PinImage.class);
        given(img2b.getId()).willReturn(8L);
        given(img2a.getImageUrl()).willReturn("img2a.jpg");
        given(pin2.getPinImages()).willReturn(List.of(img2a, img2b));

        PinImage img3a = mock(PinImage.class);
        given(img3a.getImageUrl()).willReturn("img3a.jpg"); // 선정될 이미지
        given(pin3.getPinImages()).willReturn(List.of(img3a));

        List<Pin> pinList = List.of(pin1, pin2, pin3, pin4);

        given(objectStorageService.buildImageUrl("img1b.jpg")).willReturn("http://img1b.jpg");
        given(objectStorageService.buildImageUrl("img2a.jpg")).willReturn("http://img2a.jpg");
        given(objectStorageService.buildImageUrl("img3a.jpg")).willReturn("http://img3a.jpg");

        List<String> expectedUrls = List.of("http://img1b.jpg", "http://img2a.jpg", "http://img3a.jpg");
        PlacePointResponseDto mockResponse = mock(PlacePointResponseDto.class);
        placePointDto.when(() -> PlacePointResponseDto.from(mockPlace, expectedUrls, true)).thenReturn(mockResponse);

        // when
        PlacePointResponseDto result = placeDtoConverter.convert(mockPlace, pinList, true);

        // then
        assertThat(result).isEqualTo(mockResponse);

        verify(pin1).getPinImages();
        verify(pin2).getPinImages();
        verify(pin3).getPinImages();
        verify(pin4, never()).getPinImages();

        verify(img1b).getImageUrl();
        verify(img2a).getImageUrl();
        verify(img1a, never()).getImageUrl();
        verify(img2b, never()).getImageUrl();

        verify(objectStorageService, times(3)).buildImageUrl(anyString());
        placePointDto.verify(() -> PlacePointResponseDto.from(mockPlace, expectedUrls, true));
    }

    @Test
    @DisplayName("핀 목록이 비어있으면 빈 이미지 리스트로 DTO를 생성한다")
    void convert_with_empty_pin_list() {

        // given
        Place mockPlace = mock(Place.class);
        List<Pin> pinList = Collections.emptyList();
        List<String> expectedUrls = Collections.emptyList();
        PlacePointResponseDto mockResponse = mock(PlacePointResponseDto.class);

        placePointDto.when(() -> PlacePointResponseDto.from(mockPlace, expectedUrls, true)).thenReturn(mockResponse);

        // when
        PlacePointResponseDto result = placeDtoConverter.convert(mockPlace, pinList, true);

        // then
        assertThat(result).isEqualTo(mockResponse);

        verify(objectStorageService, never()).buildImageUrl(anyString());
        placePointDto.verify(() -> PlacePointResponseDto.from(mockPlace, expectedUrls, true));
    }

    @Test
    @DisplayName("핀 중 일부에 이미지가 없으면 이미지가 있는 핀만 처리한다")
    void convert_filters_out_pins_with_no_images() {

        // given
        Place mockPlace = mock(Place.class);
        Pin pin1 = mock(Pin.class);
        Pin pin2 = mock(Pin.class);

        PinImage img1a = mock(PinImage.class);
        given(img1a.getImageUrl()).willReturn("img1a.jpg");
        given(pin1.getPinImages()).willReturn(List.of(img1a));

        given(pin2.getPinImages()).willReturn(Collections.emptyList());

        List<Pin> pinList = List.of(pin1, pin2);

        given(objectStorageService.buildImageUrl("img1a.jpg")).willReturn("http://img1a.jpg");

        List<String> expectedUrls = List.of("http://img1a.jpg");
        PlacePointResponseDto mockResponse = mock(PlacePointResponseDto.class);
        placePointDto.when(() -> PlacePointResponseDto.from(mockPlace, expectedUrls, true)).thenReturn(mockResponse);

        // when
        PlacePointResponseDto result = placeDtoConverter.convert(mockPlace, pinList, true);

        // then
        assertThat(result).isEqualTo(mockResponse);

        verify(pin1).getPinImages();
        verify(pin2).getPinImages();
        verify(objectStorageService, times(1)).buildImageUrl("img1a.jpg");
        placePointDto.verify(() -> PlacePointResponseDto.from(mockPlace, expectedUrls, true));
    }

    @Test
    @DisplayName("convertWithMarker: 빈 리스트가 주어지면 빈 리스트를 반환한다")
    void convert_with_marker_returns_empty_list() {

        // given
        List<Place> emptyPlaces = Collections.emptyList();

        // when
        List<PlaceCardMarkerResponseDto> result = placeDtoConverter.convertWithMarker(emptyPlaces);

        // then
        assertThat(result).isEmpty();

        verify(pinImageRepository, never()).findRecentImagesByPlaceIds(any());
        verify(objectStorageService, never()).buildImageUrl(anyString());

        placeCardDto.verify(() -> PlaceCardResponseDto.from(any(), anyString()), never());
        placeMarkerDto.verify(() -> PlaceMarkerResponseDto.from(any()), never());
        placeCardMarkerDto.verify(() -> PlaceCardMarkerResponseDto.of(any(), any()), never());
    }

    @Test
    @DisplayName("convertWithMarker: 장소 리스트를 카드/마커 DTO로 변환한다")
    void convert_with_marker() {

        // given
        Place mockPlace1 = mock(Place.class);
        Place mockPlace2 = mock(Place.class);
        given(mockPlace1.getId()).willReturn(1L);
        given(mockPlace2.getId()).willReturn(2L);
        List<Place> places = List.of(mockPlace1, mockPlace2);
        List<Long> placeIds = List.of(1L, 2L);

        PinImage img1 = mock(PinImage.class);
        Pin pin1 = mock(Pin.class);
        Place placeFromPin1 = mock(Place.class);
        given(img1.getPin()).willReturn(pin1);
        given(pin1.getPlace()).willReturn(placeFromPin1);
        given(placeFromPin1.getId()).willReturn(1L);
        given(img1.getImageUrl()).willReturn("img1.jpg");

        given(pinImageRepository.findRecentImagesByPlaceIds(placeIds)).willReturn(List.of(img1));
        given(objectStorageService.buildImageUrl("img1.jpg")).willReturn("http://img1.jpg");

        PlaceCardResponseDto mockCard1 = mock(PlaceCardResponseDto.class);
        PlaceCardResponseDto mockCard2 = mock(PlaceCardResponseDto.class);
        PlaceMarkerResponseDto mockMarker1 = mock(PlaceMarkerResponseDto.class);
        PlaceMarkerResponseDto mockMarker2 = mock(PlaceMarkerResponseDto.class);
        PlaceCardMarkerResponseDto mockFinalDto1 = mock(PlaceCardMarkerResponseDto.class);
        PlaceCardMarkerResponseDto mockFinalDto2 = mock(PlaceCardMarkerResponseDto.class);

        placeCardDto.when(() -> PlaceCardResponseDto.from(mockPlace1, "http://img1.jpg")).thenReturn(mockCard1);
        placeMarkerDto.when(() -> PlaceMarkerResponseDto.from(mockPlace1)).thenReturn(mockMarker1);
        placeCardMarkerDto.when(() -> PlaceCardMarkerResponseDto.of(mockCard1, mockMarker1)).thenReturn(mockFinalDto1);

        placeCardDto.when(() -> PlaceCardResponseDto.from(mockPlace2, null)).thenReturn(mockCard2);
        placeMarkerDto.when(() -> PlaceMarkerResponseDto.from(mockPlace2)).thenReturn(mockMarker2);
        placeCardMarkerDto.when(() -> PlaceCardMarkerResponseDto.of(mockCard2, mockMarker2)).thenReturn(mockFinalDto2);

        // when
        List<PlaceCardMarkerResponseDto> result = placeDtoConverter.convertWithMarker(places);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(mockFinalDto1, mockFinalDto2);

        verify(pinImageRepository).findRecentImagesByPlaceIds(placeIds);
        verify(objectStorageService).buildImageUrl("img1.jpg");

        placeCardDto.verify(() -> PlaceCardResponseDto.from(mockPlace1, "http://img1.jpg"));
        placeMarkerDto.verify(() -> PlaceMarkerResponseDto.from(mockPlace1));
        placeCardMarkerDto.verify(() -> PlaceCardMarkerResponseDto.of(mockCard1, mockMarker1));

        placeCardDto.verify(() -> PlaceCardResponseDto.from(mockPlace2, null));
        placeMarkerDto.verify(() -> PlaceMarkerResponseDto.from(mockPlace2));
        placeCardMarkerDto.verify(() -> PlaceCardMarkerResponseDto.of(mockCard2, mockMarker2));
    }
}