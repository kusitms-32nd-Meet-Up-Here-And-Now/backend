package com.meetup.hereandnow.archive.application.service;

import com.meetup.hereandnow.archive.application.service.converter.PlaceCardDtoConverterService;
import com.meetup.hereandnow.archive.dto.response.PlaceCardDto;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.place.application.service.PlaceBatchService;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import com.meetup.hereandnow.scrap.domain.PlaceScrap;
import com.meetup.hereandnow.scrap.infrastructure.repository.PlaceScrapRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArchivePlaceServiceTest {

    @Mock
    private PlaceRepository placeRepository;
    @Mock
    private PlaceScrapRepository placeScrapRepository;
    @Mock
    private PlaceBatchService placeBatchService;
    @Mock
    private PlaceCardDtoConverterService converterService;

    @InjectMocks
    private ArchivePlaceService archivePlaceService;


    @Test
    @DisplayName("내가 스크랩한 장소 조회 시 스크랩이 존재하면 DTO 리스트를 반환한다")
    void get_my_scrapped_places_when_scraps_exist() {
        // given
        Member member = mock(Member.class);
        PageRequest pageRequest = PageRequest.of(0, 10);

        Place place1 = mock(Place.class);
        Place place2 = mock(Place.class);
        PlaceScrap scrap1 = mock(PlaceScrap.class);
        PlaceScrap scrap2 = mock(PlaceScrap.class);

        given(scrap1.getPlace()).willReturn(place1);
        given(scrap2.getPlace()).willReturn(place2);

        List<PlaceScrap> scraps = List.of(scrap1, scrap2);
        Page<PlaceScrap> scrapPage = new PageImpl<>(scraps, pageRequest, scraps.size());
        List<Place> places = List.of(place1, place2);
        List<PlaceCardDto> expectedDto = List.of(mock(PlaceCardDto.class), mock(PlaceCardDto.class));

        given(placeScrapRepository.findByMemberWithPlace(member, pageRequest)).willReturn(scrapPage);
        given(converterService.toPlaceCardDtoList(places)).willReturn(expectedDto);

        // when
        List<PlaceCardDto> resultList = archivePlaceService.getMyScrappedPlaces(member, pageRequest);

        // then
        assertThat(resultList).isEqualTo(expectedDto);
        then(converterService).should(times(1)).toPlaceCardDtoList(places);
    }

    @Test
    @DisplayName("내가 스크랩한 장소 조회 시 스크랩이 없으면 빈 리스트를 반환한다")
    void get_my_scrapped_places_when_scraps_empty() {
        // given
        Member member = mock(Member.class);
        PageRequest pageRequest = PageRequest.of(0, 10);

        given(placeScrapRepository.findByMemberWithPlace(member, pageRequest)).willReturn(Page.empty(pageRequest));

        // when
        List<PlaceCardDto> resultList = archivePlaceService.getMyScrappedPlaces(member, pageRequest);

        // then
        assertThat(resultList).isEmpty();
        then(converterService).should(never()).toPlaceCardDtoList(any());
    }

    @Test
    @DisplayName("장소 평점/태그 업데이트 스케줄러 실행 시 여러 페이지가 있으면 모두 처리한다")
    void update_place_rating_and_tags() {
        // given
        PageRequest page0 = PageRequest.of(0, 1000);
        PageRequest page1 = PageRequest.of(1, 1000);

        List<Long> idsPage1 = List.of(1L, 2L);
        List<Long> idsPage2 = List.of(3L, 4L);

        Page<Long> idPage1 = new PageImpl<>(idsPage1, page0, 1002);
        Page<Long> idPage2 = new PageImpl<>(idsPage2, page1, 1002);

        given(placeRepository.findAllIds(page0)).willReturn(idPage1);
        given(placeRepository.findAllIds(page1)).willReturn(idPage2);

        // when
        archivePlaceService.updatePlaceRatingAndTags();

        // then
        then(placeRepository).should(times(1)).findAllIds(page0);
        then(placeRepository).should(times(1)).findAllIds(page1);
        then(placeBatchService).should(times(1)).process(idsPage1);
        then(placeBatchService).should(times(1)).process(idsPage2);
    }

    @Test
    @DisplayName("장소 평점/태그 업데이트 스케줄러 실행 시 장소가 없으면 배치를 호출하지 않는다")
    void update_place_rating_and_tags_no_places() {
        // given
        PageRequest page0 = PageRequest.of(0, 1000);
        Page<Long> emptyPage = Page.empty(page0);

        given(placeRepository.findAllIds(page0)).willReturn(emptyPage);

        // when
        archivePlaceService.updatePlaceRatingAndTags();

        // then
        then(placeRepository).should(times(1)).findAllIds(page0);
        then(placeBatchService).should(never()).process(any());
    }
}
