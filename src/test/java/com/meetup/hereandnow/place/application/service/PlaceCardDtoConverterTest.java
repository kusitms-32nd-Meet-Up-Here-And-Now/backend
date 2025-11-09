package com.meetup.hereandnow.place.application.service;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import com.meetup.hereandnow.pin.infrastructure.repository.PinImageRepository;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.PlaceCardResponseDto;
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
class PlaceCardDtoConverterTest {

    @Mock
    private PinImageRepository pinImageRepository;

    @Mock
    private ObjectStorageService objectStorageService;

    @InjectMocks
    private PlaceCardDtoConverter placeCardDtoConverter;

    private MockedStatic<PlaceCardResponseDto> mockedDto;

    @BeforeEach
    void setUp() {
        mockedDto = mockStatic(PlaceCardResponseDto.class);
    }

    @AfterEach
    void tearDown() {
        mockedDto.close();
    }

    @Test
    @DisplayName("빈 리스트가 주어지면 빈 리스트를 반환한다")
    void convert_with_empty_list_returns_empty_list() {

        // given
        List<Place> emptyList = Collections.emptyList();

        // when
        List<PlaceCardResponseDto> result = placeCardDtoConverter.convert(emptyList);

        // then
        assertThat(result).isEmpty();

        verify(pinImageRepository, never()).findRecentImagesByPlaceIds(any());
        verify(objectStorageService, never()).buildImageUrl(anyString());
        mockedDto.verify(() -> PlaceCardResponseDto.from(any(), anyString()), never());
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
        mockedDto.when(() -> PlaceCardResponseDto.from(mockPlace1, "http://img1.jpg")).thenReturn(dto1);
        mockedDto.when(() -> PlaceCardResponseDto.from(mockPlace2, "http://img2.jpg")).thenReturn(dto2);

        // when
        List<PlaceCardResponseDto> result = placeCardDtoConverter.convert(places);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(dto1, dto2);

        verify(pinImageRepository).findRecentImagesByPlaceIds(placeIds);
        verify(objectStorageService).buildImageUrl("img1.jpg");
        verify(objectStorageService).buildImageUrl("img2.jpg");
        mockedDto.verify(() -> PlaceCardResponseDto.from(mockPlace1, "http://img1.jpg"));
        mockedDto.verify(() -> PlaceCardResponseDto.from(mockPlace2, "http://img2.jpg"));
    }
}