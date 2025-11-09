package com.meetup.hereandnow.scrap.application.facade;

import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.application.service.view.CourseCardDtoConverter;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.response.CourseCardResponseDto;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.place.application.service.PlaceCardDtoConverter;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.PlaceCardResponseDto;
import com.meetup.hereandnow.scrap.application.service.CourseScrapService;
import com.meetup.hereandnow.scrap.application.service.PlaceScrapService;
import com.meetup.hereandnow.scrap.domain.CourseScrap;
import com.meetup.hereandnow.scrap.domain.PlaceScrap;
import com.meetup.hereandnow.scrap.dto.response.ScrapResponseDto;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScrapFacadeTest {

    @Mock
    private CourseScrapService courseScrapService;

    @Mock
    private PlaceScrapService placeScrapService;

    @Mock
    private CourseCardDtoConverter courseCardDtoConverter;

    @Mock
    private PlaceCardDtoConverter placeCardDtoConverter;

    @InjectMocks
    private ScrapFacade scrapFacade;

    private MockedStatic<SecurityUtils> mockSecurityUtils;
    private Member mockMember;

    @BeforeEach
    void setUp() {
        mockSecurityUtils = mockStatic(SecurityUtils.class);
        mockMember = Member.builder().id(1L).nickname("nickname").build();
    }

    @AfterEach
    void tearDown() {
        mockSecurityUtils.close();
    }

    @Test
    @DisplayName("코스 스크랩 토글 시 스크랩 서비스를 호출한다")
    void toggle_scrap_course() {

        // given
        Long courseId = 1L;
        ScrapResponseDto expectedDto = mock(ScrapResponseDto.class);

        mockSecurityUtils.when(SecurityUtils::getCurrentMember).thenReturn(mockMember);
        given(courseScrapService.toggleScrapCourse(mockMember, courseId)).willReturn(expectedDto);

        // when
        ScrapResponseDto responseDto = scrapFacade.toggleScrapCourse(courseId);

        // then
        assertThat(responseDto).isEqualTo(expectedDto);
        then(courseScrapService).should().toggleScrapCourse(mockMember, courseId);
    }

    @Test
    @DisplayName("장소 스크랩 토글 시 스크랩 서비스를 호출한다")
    void toggle_scrap_place() {

        // given
        Long placeId = 1L;
        ScrapResponseDto expectedDto = mock(ScrapResponseDto.class);

        mockSecurityUtils.when(SecurityUtils::getCurrentMember).thenReturn(mockMember);
        given(placeScrapService.toggleScrapPlace(mockMember, placeId)).willReturn(expectedDto);

        // when
        ScrapResponseDto responseDto = scrapFacade.toggleScrapPlace(placeId);

        // then
        assertThat(responseDto).isEqualTo(expectedDto);
        then(placeScrapService).should().toggleScrapPlace(mockMember, placeId);
    }

    @Test
    @DisplayName("스크랩한 코스 목록을 DTO로 변환하여 반환한다")
    void get_scrapped_courses() {

        // given
        int page = 0;
        int size = 20;
        String sort = "recent";

        Pageable mockPageable = mock(Pageable.class);

        CourseScrap scrap1 = mock(CourseScrap.class);
        Course course1 = mock(Course.class);
        Page<CourseScrap> scrapPage = new PageImpl<>(List.of(scrap1));
        List<Course> courses = List.of(course1);
        List<CourseCardResponseDto> expectedDtos = List.of(mock(CourseCardResponseDto.class));

        mockSecurityUtils.when(SecurityUtils::getCurrentMember).thenReturn(mockMember);
        given(courseScrapService.resolveSort(page, size, sort)).willReturn(mockPageable);
        given(courseScrapService.getScrapsByMember(mockMember, mockPageable)).willReturn(scrapPage);
        given(scrap1.getCourse()).willReturn(course1);
        given(courseCardDtoConverter.convert(courses)).willReturn(expectedDtos);

        // when
        List<CourseCardResponseDto> result = scrapFacade.getScrappedCourses(page, size, sort);

        // then
        assertThat(result).isEqualTo(expectedDtos);
        verify(courseScrapService).resolveSort(page, size, sort);
        verify(courseScrapService).getScrapsByMember(mockMember, mockPageable);
        verify(scrap1).getCourse();
        verify(courseCardDtoConverter).convert(courses);
    }

    @Test
    @DisplayName("스크랩한 장소 목록을 DTO로 변환하여 반환한다")
    void get_scrapped_places() {

        // given
        int page = 0;
        int size = 20;
        String sort = "recent";

        Pageable mockPageable = mock(Pageable.class);

        PlaceScrap scrap1 = mock(PlaceScrap.class);
        Place place1 = mock(Place.class);
        Page<PlaceScrap> scrapPage = new PageImpl<>(List.of(scrap1));
        List<Place> places = List.of(place1);
        List<PlaceCardResponseDto> expectedDtos = List.of(mock(PlaceCardResponseDto.class));

        mockSecurityUtils.when(SecurityUtils::getCurrentMember).thenReturn(mockMember);
        given(placeScrapService.resolveSort(page, size, sort)).willReturn(mockPageable);
        given(placeScrapService.getScrapsByMember(mockMember, mockPageable)).willReturn(scrapPage);
        given(scrap1.getPlace()).willReturn(place1);
        given(placeCardDtoConverter.convert(places)).willReturn(expectedDtos);

        // when
        List<PlaceCardResponseDto> result = scrapFacade.getScrappedPlaces(page, size, sort);

        // then
        assertThat(result).isEqualTo(expectedDtos);
        verify(placeScrapService).resolveSort(page, size, sort);
        verify(placeScrapService).getScrapsByMember(mockMember, mockPageable);
        verify(scrap1).getPlace();
        verify(placeCardDtoConverter).convert(places);
    }
}