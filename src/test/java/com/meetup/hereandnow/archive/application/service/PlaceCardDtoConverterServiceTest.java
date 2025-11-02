package com.meetup.hereandnow.archive.application.service;

import com.meetup.hereandnow.archive.application.service.converter.PlaceCardDtoConverterService;
import com.meetup.hereandnow.archive.dto.response.PlaceCardDto;
import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.pin.dto.PlaceIdWithImage;
import com.meetup.hereandnow.pin.infrastructure.repository.PinImageRepository;
import com.meetup.hereandnow.place.domain.Place;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PlaceCardDtoConverterServiceTest {

    @Mock
    private PinImageRepository pinImageRepository;
    @Mock
    private ObjectStorageService objectStorageService;

    @InjectMocks
    private PlaceCardDtoConverterService sut;

    @Test
    @DisplayName("Place 리스트가 주어지면 DTO 리스트로 변환해 제공한다")
    void convert_to_place_card_dto_list() {
        // given
        Place place1 = mock(Place.class);
        Place place2 = mock(Place.class);
        List<Place> places = List.of(place1, place2);
        given(place1.getId()).willReturn(1L);
        given(place2.getId()).willReturn(2L);

        PlaceIdWithImage img1 = mock(PlaceIdWithImage.class);
        given(img1.getImageUrl()).willReturn("img1");
        given(img1.getPlaceId()).willReturn(1L);
        given(place1.getPlaceRating()).willReturn(BigDecimal.valueOf(3.5));
        given(place1.getPlaceTags()).willReturn(List.of("tag1", "tag2"));

        PlaceIdWithImage img2 = mock(PlaceIdWithImage.class);
        given(img2.getImageUrl()).willReturn("img2");
        given(img2.getPlaceId()).willReturn(2L);
        given(place2.getPlaceRating()).willReturn(BigDecimal.valueOf(4.5));
        given(place2.getPlaceTags()).willReturn(List.of("tag3", "tag4"));

        given(pinImageRepository.findImageUrlsByPlaceIds(List.of(place1.getId(), place2.getId())))
                .willReturn(List.of(img1, img2));
        given(objectStorageService.buildImageUrl("img1")).willReturn("domain/img1");
        given(objectStorageService.buildImageUrl("img2")).willReturn("domain/img2");

        // when
        List<PlaceCardDto> resultList = sut.toPlaceCardDtoList(places);

        // then
        assertThat(resultList.get(0).imageUrl()).isEqualTo(List.of("domain/img1"));
        assertThat(resultList.get(0).placeTag()).isEqualTo(List.of("tag1", "tag2"));
        assertThat(resultList.get(1).imageUrl()).isEqualTo(List.of("domain/img2"));
        assertThat(resultList.get(1).placeTag()).isEqualTo(List.of("tag3", "tag4"));
        then(pinImageRepository).should(times(1))
                .findImageUrlsByPlaceIds(List.of(place1.getId(), place2.getId()));
    }

    @Test
    @DisplayName("평점, 태그가 아직 계산되지 않았으면 0점과 빈 태그 리스트를 반환한다")
    void convert_to_place_card_dto_list_without_ratings_and_tags() {
        // given
        Place place = mock(Place.class);
        given(place.getPlaceRating()).willReturn(null);
        given(place.getPlaceTags()).willReturn(null);
        given(pinImageRepository.findImageUrlsByPlaceIds(List.of(place.getId())))
                .willReturn(List.of(mock(PlaceIdWithImage.class)));

        // when
        List<PlaceCardDto> resultList = sut.toPlaceCardDtoList(List.of(place));

        // then
        assertThat(resultList.getFirst().placeTag()).isEqualTo(List.of());
        assertThat(resultList.getFirst().placeRating()).isEqualTo(0.0);
    }
}