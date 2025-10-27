package com.meetup.hereandnow.scrap.application.facade;

import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.scrap.application.service.CourseScrapService;
import com.meetup.hereandnow.scrap.application.service.PlaceScrapService;
import com.meetup.hereandnow.scrap.domain.CourseScrap;
import com.meetup.hereandnow.scrap.domain.PlaceScrap;
import com.meetup.hereandnow.scrap.dto.response.ScrapResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ScrapFacadeTest {

    @Mock
    private CourseScrapService courseScrapService;
    @Mock
    private PlaceScrapService placeScrapService;
    @InjectMocks
    private ScrapFacade scrapFacade;

    @Test
    @DisplayName("코스 스크랩 토글 시 스크랩이 존재하지 않는 경우 스크랩 서비스를 호출한다")
    void call_course_scrap_when_does_not_exist() {
        // given
        Long courseId = 1L;
        var member = mock(Member.class);
        ScrapResponseDto expectedDto = mock(ScrapResponseDto.class);

        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(member);
            given(courseScrapService.findOptional(member, courseId)).willReturn(Optional.empty());
            given(courseScrapService.scrap(member, courseId)).willReturn(expectedDto);

            // when
            ScrapResponseDto responseDto = scrapFacade.toggleScrapCourse(courseId);

            // then
            assertThat(responseDto).isEqualTo(expectedDto);
            then(courseScrapService).should().findOptional(member, courseId);
            then(courseScrapService).should().scrap(member, courseId);
            then(courseScrapService).should(never()).deleteScrap(any(CourseScrap.class));
        }
    }

    @Test
    @DisplayName("코스 스크랩 토글 시 스크랩이 존재하는 경우 스크랩 삭제 서비스를 호출한다")
    void call_course_delete_scrap_when_exists() {
        // given
        Long courseId = 1L;
        Member member = mock(Member.class);
        CourseScrap courseScrap = mock(CourseScrap.class);
        ScrapResponseDto expectedDto = mock(ScrapResponseDto.class);

        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(member);
            given(courseScrapService.findOptional(member, courseId)).willReturn(Optional.of(courseScrap));
            given(courseScrapService.deleteScrap(courseScrap)).willReturn(expectedDto);

            // when
            ScrapResponseDto responseDto = scrapFacade.toggleScrapCourse(courseId);

            // then
            assertThat(responseDto).isEqualTo(expectedDto);
            then(courseScrapService).should().findOptional(member, courseId);
            then(courseScrapService).should(never()).scrap(any(Member.class), anyLong());
            then(courseScrapService).should().deleteScrap(courseScrap);
        }
    }

    @Test
    @DisplayName("장소 스크랩 토글 시 스크랩이 존재하지 않는 경우 스크랩 서비스를 호출한다")
    void call_place_scrap_when_does_not_exist() {
        // given
        Long placeId = 1L;
        Member member = mock(Member.class);
        ScrapResponseDto expectedDto = mock(ScrapResponseDto.class);

        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(member);
            given(placeScrapService.findOptional(member, placeId)).willReturn(Optional.empty());
            given(placeScrapService.scrap(member, placeId)).willReturn(expectedDto);

            // when
            ScrapResponseDto responseDto = scrapFacade.toggleScrapPlace(placeId);

            // then
            assertThat(responseDto).isEqualTo(expectedDto);
            then(placeScrapService).should().findOptional(member, placeId);
            then(placeScrapService).should().scrap(member, placeId);
            then(placeScrapService).should(never()).deleteScrap(any(PlaceScrap.class));
        }
    }

    @Test
    @DisplayName("장소 스크랩 토글 시 스크랩이 존재하는 경우 스크랩 삭제 서비스를 호출한다")
    void call_place_delete_scrap_when_exists() {
        // given
        Long placeId = 1L;
        Member member = mock(Member.class);
        PlaceScrap placeScrap = mock(PlaceScrap.class);
        ScrapResponseDto expectedDto = mock(ScrapResponseDto.class);

        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(member);
            given(placeScrapService.findOptional(member, placeId)).willReturn(Optional.of(placeScrap));
            given(placeScrapService.deleteScrap(placeScrap)).willReturn(expectedDto);

            // when
            ScrapResponseDto responseDto = scrapFacade.toggleScrapPlace(placeId);

            // then
            assertThat(responseDto).isEqualTo(expectedDto);
            then(placeScrapService).should().findOptional(member, placeId);
            then(placeScrapService).should(never()).scrap(any(Member.class), anyLong());
            then(placeScrapService).should().deleteScrap(placeScrap);
        }
    }
}