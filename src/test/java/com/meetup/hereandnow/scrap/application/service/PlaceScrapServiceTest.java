package com.meetup.hereandnow.scrap.application.service;

import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.infrastructure.repository.PlaceRepository;
import com.meetup.hereandnow.scrap.domain.PlaceScrap;
import com.meetup.hereandnow.scrap.dto.response.ScrapResponseDto;
import com.meetup.hereandnow.scrap.exception.ScrapErrorCode;
import com.meetup.hereandnow.scrap.infrastructure.repository.PlaceScrapRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceScrapServiceTest {

    @Mock
    private PlaceScrapRepository placeScrapRepository;
    @Mock
    private PlaceRepository placeRepository;
    @InjectMocks
    private PlaceScrapService placeScrapService;

    private MockedStatic<ScrapResponseDto> mockedScrapDto;

    private Member mockMember;
    private Place mockPlace;

    @BeforeEach
    void set_up() {
        mockMember = mock(Member.class);
        mockPlace = mock(Place.class);
        mockedScrapDto = mockStatic(ScrapResponseDto.class);
    }

    @AfterEach
    void tear_down() {
        mockedScrapDto.close();
    }

    @Test
    @DisplayName("스크랩 생성: 기존 스크랩이 없을 때 스크랩을 생성하고 DTO를 반환한다")
    void toggle_scrap_place_creates_scrap_when_not_exists() {

        // given
        Long placeId = 1L;
        Long memberId = 10L;
        ScrapResponseDto mockResponse = mock(ScrapResponseDto.class);

        given(placeRepository.findByIdWithLock(placeId)).willReturn(Optional.of(mockPlace));
        given(mockMember.getId()).willReturn(memberId);
        given(placeScrapRepository.findByMemberIdAndPlaceId(memberId, placeId)).willReturn(Optional.empty());
        mockedScrapDto.when(() -> ScrapResponseDto.from(any(PlaceScrap.class))).thenReturn(mockResponse);

        // when
        ScrapResponseDto result = placeScrapService.toggleScrapPlace(mockMember, placeId);

        // then
        assertThat(result).isEqualTo(mockResponse);

        verify(mockPlace).incrementScrapCount();
        verify(placeScrapRepository).save(any(PlaceScrap.class));

        verify(mockPlace, never()).decrementScrapCount();
        verify(placeScrapRepository, never()).delete(any());
    }

    @Test
    @DisplayName("스크랩 삭제: 기존 스크랩이 있을 때 스크랩을 삭제하고 DTO를 반환한다")
    void toggle_scrap_place_deletes_scrap_when_exists() {

        // given
        Long placeId = 1L;
        Long memberId = 10L;
        PlaceScrap mockScrap = mock(PlaceScrap.class);
        ScrapResponseDto mockResponse = mock(ScrapResponseDto.class);

        given(placeRepository.findByIdWithLock(placeId)).willReturn(Optional.of(mockPlace));
        given(mockMember.getId()).willReturn(memberId);
        given(placeScrapRepository.findByMemberIdAndPlaceId(memberId, placeId)).willReturn(Optional.of(mockScrap));
        mockedScrapDto.when(ScrapResponseDto::from).thenReturn(mockResponse);

        // when
        ScrapResponseDto result = placeScrapService.toggleScrapPlace(mockMember, placeId);

        // then
        assertThat(result).isEqualTo(mockResponse);

        verify(mockPlace).decrementScrapCount();
        verify(placeScrapRepository).delete(mockScrap);

        verify(mockPlace, never()).incrementScrapCount();
        verify(placeScrapRepository, never()).save(any());
    }

    @Test
    @DisplayName("스크랩 토글 실패: 장소가 존재하지 않으면 예외를 던진다")
    void toggle_scrap_place_throws_exception_when_place_not_found() {

        // given
        Long placeId = 99L;

        given(placeRepository.findByIdWithLock(placeId)).willReturn(Optional.empty());
        Class<? extends RuntimeException> expectedException = ScrapErrorCode.PLACE_NOT_FOUND.toException().getClass();

        // when, then
        assertThatThrownBy(() -> placeScrapService.toggleScrapPlace(mockMember, placeId))
                .isInstanceOf(expectedException);

        verify(mockMember, never()).getId();
        verify(placeScrapRepository, never()).findByMemberIdAndPlaceId(any(), any());
        verify(placeScrapRepository, never()).save(any());
        verify(placeScrapRepository, never()).delete(any());
        verify(mockPlace, never()).incrementScrapCount();
        verify(mockPlace, never()).decrementScrapCount();

        mockedScrapDto.verify(ScrapResponseDto::from, never());
        mockedScrapDto.verify(() -> ScrapResponseDto.from(any(PlaceScrap.class)), never());
    }

    @Test
    @DisplayName("멤버별 스크랩 조회: 레포지토리의 조회 메서드를 올바르게 호출한다")
    void get_scraps_by_member_calls_repository() {

        // given
        Pageable mockPageable = mock(Pageable.class);
        Page<PlaceScrap> mockPage = new PageImpl<>(List.of(mock(PlaceScrap.class)));

        given(placeScrapRepository.findScrapsByMemberWithSort(mockMember, mockPageable)).willReturn(mockPage);

        // when
        Page<PlaceScrap> result = placeScrapService.getScrapsByMember(mockMember, mockPageable);

        // then
        assertThat(result).isEqualTo(mockPage);
        verify(placeScrapRepository).findScrapsByMemberWithSort(mockMember, mockPageable);
    }
}