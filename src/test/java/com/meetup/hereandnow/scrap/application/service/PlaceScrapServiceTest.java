package com.meetup.hereandnow.scrap.application.service;

import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import com.meetup.hereandnow.scrap.domain.PlaceScrap;
import com.meetup.hereandnow.scrap.dto.response.ScrapResponseDto;
import com.meetup.hereandnow.scrap.repository.PlaceScrapRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class PlaceScrapServiceTest {

    @Mock
    private PlaceScrapRepository placeScrapRepository;
    @Mock
    private PlaceRepository placeRepository;
    @InjectMocks
    private PlaceScrapService placeScrapService;

    @Test
    @DisplayName("findOptional 호출 시 스크랩이 존재하면 PlaceScrap을 반환한다")
    void findOptional_when_scrap_exists_then_return_scrap() {
        // given
        Member member = mock(Member.class);
        Long memberId = 1L;
        Long placeId = 20L;
        PlaceScrap placeScrap = mock(PlaceScrap.class);

        given(member.getId()).willReturn(memberId);
        given(placeScrapRepository.findByMemberIdAndPlaceId(memberId, placeId))
                .willReturn(Optional.of(placeScrap));

        // when
        Optional<PlaceScrap> result = placeScrapService.findOptional(member, placeId);

        // then
        assertThat(result).isPresent().contains(placeScrap);
    }

    @Test
    @DisplayName("스크랩 시 장소가 존재하면 스크랩을 저장한다")
    void scrap_when_place_exists_then_save_scrap() {
        // given
        Member member = mock(Member.class);
        Long placeId = 20L;
        Place place = mock(Place.class);

        given(placeRepository.findById(placeId)).willReturn(Optional.of(place));

        // when
        ScrapResponseDto responseDto = placeScrapService.scrap(member, placeId);

        // then
        assertThat(responseDto).isNotNull();
        then(placeRepository).should().findById(placeId);
        then(placeScrapRepository).should().save(any(PlaceScrap.class));
    }

    @Test
    @DisplayName("스크랩 시 장소가 존재하지 않으면 예외를 발생시킨다")
    void scrap_when_place_does_not_exist_then_throw_exception() {
        // given
        Member member = mock(Member.class);
        Long placeId = 20L;

        given(placeRepository.findById(placeId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> placeScrapService.scrap(member, placeId))
                .isInstanceOf(DomainException.class);

        then(placeRepository).should().findById(placeId);
        then(placeScrapRepository).should(never()).save(any(PlaceScrap.class));
    }

    @Test
    @DisplayName("스크랩을 삭제한다")
    void deleteScrap() {
        // given
        PlaceScrap placeScrap = mock(PlaceScrap.class);

        // when
        ScrapResponseDto responseDto = placeScrapService.deleteScrap(placeScrap);

        // then
        assertThat(responseDto).isNotNull();
        then(placeScrapRepository).should().delete(placeScrap);
    }
}
