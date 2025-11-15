package com.meetup.hereandnow.place.application.service;

import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import com.meetup.hereandnow.place.infrastructure.specification.PlaceSpecifications;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.eq;

@ExtendWith(MockitoExtension.class)
class PlaceSearchServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @InjectMocks
    private PlaceSearchService placeSearchService;

    private MockedStatic<PlaceSpecifications> mockedSpec;

    private Pageable mockPageable;
    private Page<Place> mockPage;
    private Specification<Place> dummySpec;

    @BeforeEach
    void setUp() {
        mockedSpec = mockStatic(PlaceSpecifications.class);
        mockPageable = mock(Pageable.class);
        mockPage = new PageImpl<>(Collections.emptyList());
        dummySpec = mock(Specification.class);
    }

    @AfterEach
    void tearDown() {
        mockedSpec.close();
    }

    @Test
    @DisplayName("searchPlaces: 모든 필터가 제공되면 모든 Specification 메서드를 호출한다")
    void search_places_when_all_filters_provided() {

        // given
        Integer rating = 4;
        List<String> keywords = List.of("카페");
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        String with = "친구";
        String region = "서울";
        List<String> placeCode = List.of("FD6");
        List<String> tags = List.of("사진찍기 좋아요");

        mockedSpec.when(() -> PlaceSpecifications.isRatingInRange(anyInt())).thenReturn(dummySpec);
        mockedSpec.when(() -> PlaceSpecifications.containsKeywords(anyList())).thenReturn(dummySpec);
        mockedSpec.when(() -> PlaceSpecifications.isPinnedByCourseInDateRange(any(), any())).thenReturn(dummySpec);
        mockedSpec.when(() -> PlaceSpecifications.isPinnedByCourseWith(anyString())).thenReturn(dummySpec);
        mockedSpec.when(() -> PlaceSpecifications.isPinnedByCourseInRegion(anyString())).thenReturn(dummySpec);
        mockedSpec.when(() -> PlaceSpecifications.hasPlaceGroupCodeIn(anyList())).thenReturn(dummySpec);
        mockedSpec.when(() -> PlaceSpecifications.hasTagIn(anyList())).thenReturn(dummySpec);

        given(placeRepository.findAll(any(Specification.class), eq(mockPageable))).willReturn(mockPage);

        // when
        Page<Place> result = placeSearchService.searchPlaces(
                rating, keywords, startDate, endDate, with, region, placeCode, tags, mockPageable
        );

        // then
        assertThat(result).isEqualTo(mockPage);

        mockedSpec.verify(() -> PlaceSpecifications.isRatingInRange(rating));
        mockedSpec.verify(() -> PlaceSpecifications.containsKeywords(keywords));
        mockedSpec.verify(() -> PlaceSpecifications.isPinnedByCourseInDateRange(startDate, endDate));
        mockedSpec.verify(() -> PlaceSpecifications.isPinnedByCourseWith(with));
        mockedSpec.verify(() -> PlaceSpecifications.isPinnedByCourseInRegion(region));
        mockedSpec.verify(() -> PlaceSpecifications.hasPlaceGroupCodeIn(placeCode));
        mockedSpec.verify(() -> PlaceSpecifications.hasTagIn(tags));

        verify(placeRepository).findAll(any(Specification.class), eq(mockPageable));
    }

    @Test
    @DisplayName("searchPlaces: 필터가 null이거나 비어있으면 Specification 메서드를 호출하지 않는다")
    void search_places_when_filters_are_null_or_empty() {

        // given
        given(placeRepository.findAll(any(Specification.class), eq(mockPageable))).willReturn(mockPage);

        // when
        Page<Place> result = placeSearchService.searchPlaces(
                null, null, null, null, null, null, null, null, mockPageable
        );

        // then
        assertThat(result).isEqualTo(mockPage);

        mockedSpec.verifyNoInteractions();
        verify(placeRepository).findAll(any(Specification.class), eq(mockPageable));
    }

    @Test
    @DisplayName("searchPlaces: 유효하지 않은 필터 값(0, 공백 등)은 무시한다")
    void search_places_ignores_invalid_filter_values() {

        // given
        Integer rating = 0;
        List<String> keywords = Collections.emptyList();
        String with = " ";
        String region = "";
        List<String> placeCode = Collections.emptyList();
        List<String> tags = Collections.emptyList();

        given(placeRepository.findAll(any(Specification.class), eq(mockPageable))).willReturn(mockPage);

        // when
        Page<Place> result = placeSearchService.searchPlaces(
                rating, keywords, null, null, with, region, placeCode, tags, mockPageable
        );

        // then
        assertThat(result).isEqualTo(mockPage);

        mockedSpec.verifyNoInteractions();
        verify(placeRepository).findAll(any(Specification.class), eq(mockPageable));
    }

    @Test
    @DisplayName("searchPlaces: 날짜 필터 중 하나만 있어도 Specification을 호출한다")
    void search_places_when_only_one_date_exists() {

        // given
        LocalDate startDate = LocalDate.of(2024, 1, 1);

        mockedSpec.when(() -> PlaceSpecifications.isPinnedByCourseInDateRange(any(), any())).thenReturn(dummySpec);
        given(placeRepository.findAll(any(Specification.class), eq(mockPageable))).willReturn(mockPage);

        // when
        placeSearchService.searchPlaces(
                null, null, startDate, null, null, null, null, null, mockPageable
        );

        // then
        mockedSpec.verify(() -> PlaceSpecifications.isPinnedByCourseInDateRange(startDate, null));
        mockedSpec.verify(() -> PlaceSpecifications.isRatingInRange(anyInt()), never());
        mockedSpec.verify(() -> PlaceSpecifications.containsKeywords(anyList()), never());
    }
}