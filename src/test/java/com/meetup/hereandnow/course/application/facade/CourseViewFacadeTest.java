package com.meetup.hereandnow.course.application.facade;

import com.meetup.hereandnow.core.infrastructure.value.SortType;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.core.util.SortUtils;
import com.meetup.hereandnow.course.application.service.search.CourseSearchService;
import com.meetup.hereandnow.course.application.service.view.CourseCardDtoConverter;
import com.meetup.hereandnow.course.application.service.view.CourseDetailsViewService;
import com.meetup.hereandnow.course.application.service.view.CourseFindService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.response.*;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.dto.PinDetailsResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseViewFacadeTest {

    @Mock
    private CourseDetailsViewService courseDetailsViewService;

    @Mock
    private CourseFindService courseFindService;

    @Mock
    private CourseCardDtoConverter courseCardDtoConverter;

    @Mock
    private CourseSearchService courseSearchService;

    @InjectMocks
    private CourseViewFacade courseViewFacade;

    private MockedStatic<SecurityUtils> mockedSecurity;
    private MockedStatic<CourseDetailsResponseDto> mockedDto;
    private MockedStatic<SortUtils> mockedSortUtils;

    @BeforeEach
    void setUp() {
        mockedSecurity = Mockito.mockStatic(SecurityUtils.class);
        mockedDto = Mockito.mockStatic(CourseDetailsResponseDto.class);
        mockedSortUtils = Mockito.mockStatic(SortUtils.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurity.close();
        mockedDto.close();
        mockedSortUtils.close();
    }

    @Test
    @DisplayName("코스 상세 조회하면 핀 목록 dto와 함께 반환된다.")
    void get_course_details() {

        // given
        Long courseId = 1L;
        Member mockMember = mock(Member.class);
        Course mockCourse = mock(Course.class);
        Pin pin1 = mock(Pin.class);
        Pin pin2 = mock(Pin.class);
        List<Pin> pinList = List.of(pin1, pin2);

        Set<Long> scrappedPlaceIds = Set.of(10L);
        PinDetailsResponseDto pinDto1 = mock(PinDetailsResponseDto.class);
        PinDetailsResponseDto pinDto2 = mock(PinDetailsResponseDto.class);
        List<PinDetailsResponseDto> pinDtoList = List.of(pinDto1, pinDto2);

        CourseDetailsResponseDto expectedDto = mock(CourseDetailsResponseDto.class);

        mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(mockMember);

        given(mockCourse.getMember()).willReturn(mockMember);
        given(courseDetailsViewService.getCourseById(courseId)).willReturn(Optional.of(mockCourse));
        given(mockCourse.getPinList()).willReturn(pinList);
        given(courseDetailsViewService.getScrappedPlaceIds(mockMember, mockCourse)).willReturn(scrappedPlaceIds);

        given(courseDetailsViewService.toPinDetailsDto(pin1, 1, scrappedPlaceIds)).willReturn(pinDto1);
        given(courseDetailsViewService.toPinDetailsDto(pin2, 2, scrappedPlaceIds)).willReturn(pinDto2);

        mockedDto.when(() -> CourseDetailsResponseDto.of(mockMember, mockCourse, pinDtoList)).thenReturn(expectedDto);

        // when
        CourseDetailsResponseDto result = courseViewFacade.getCourseDetails(courseId);

        // then
        assertThat(result).isEqualTo(expectedDto);

        mockedSecurity.verify(SecurityUtils::getCurrentMember);
        verify(courseDetailsViewService).getCourseById(courseId);
        verify(courseDetailsViewService).getScrappedPlaceIds(mockMember, mockCourse);
        verify(courseDetailsViewService).toPinDetailsDto(pin1, 1, scrappedPlaceIds);
        verify(courseDetailsViewService).toPinDetailsDto(pin2, 2, scrappedPlaceIds);
        mockedDto.verify(() -> CourseDetailsResponseDto.of(mockMember, mockCourse, pinDtoList));
    }

    @Test
    @DisplayName("코스 상세 조회 시 존재하지 않는 코스면 예외를 발생시킨다")
    void get_course_details_not_found() {

        // given
        Long courseId = 1L;
        Member mockMember = mock(Member.class);
        var expectedException = CourseErrorCode.NOT_FOUND_COURSE.toException();

        mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(mockMember);
        given(courseDetailsViewService.getCourseById(courseId)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> courseViewFacade.getCourseDetails(courseId))
                .isInstanceOf(expectedException.getClass())
                .hasMessageContaining(expectedException.getMessage());

        mockedSecurity.verify(SecurityUtils::getCurrentMember);
        verify(courseDetailsViewService).getCourseById(courseId);
        verify(courseDetailsViewService, never()).getScrappedPlaceIds(any(), any());
    }

    @Test
    @DisplayName("getPinDtoList는 각 핀의 인덱스와 함께 dto 리스트를 반환한다.")
    void get_pin_dto_list() {

        // given
        Member mockMember = mock(Member.class);
        Course mockCourse = mock(Course.class);
        Pin pin1 = mock(Pin.class);
        Pin pin2 = mock(Pin.class);
        List<Pin> pinList = List.of(pin1, pin2);

        Set<Long> scrappedPlaceIds = Set.of(10L);
        PinDetailsResponseDto pinDto1 = mock(PinDetailsResponseDto.class);
        PinDetailsResponseDto pinDto2 = mock(PinDetailsResponseDto.class);

        given(mockCourse.getPinList()).willReturn(pinList);
        given(courseDetailsViewService.getScrappedPlaceIds(mockMember, mockCourse)).willReturn(scrappedPlaceIds);

        given(courseDetailsViewService.toPinDetailsDto(pin1, 1, scrappedPlaceIds)).willReturn(pinDto1);
        given(courseDetailsViewService.toPinDetailsDto(pin2, 2, scrappedPlaceIds)).willReturn(pinDto2);

        // when
        List<PinDetailsResponseDto> result = courseViewFacade.getPinDtoList(mockMember, mockCourse);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(pinDto1, pinDto2);

        verify(courseDetailsViewService).getScrappedPlaceIds(mockMember, mockCourse);
        verify(courseDetailsViewService).toPinDetailsDto(pin1, 1, scrappedPlaceIds);
        verify(courseDetailsViewService).toPinDetailsDto(pin2, 2, scrappedPlaceIds);
    }

    @Test
    @DisplayName("getRecommendedCourses: 주변 코스를 조회하여 DTO 리스트로 변환한다")
    void get_recommended_courses() {

        // given
        int page = 0;
        int size = 10;
        SortType sort = SortType.SCRAPS;
        double lat = 37.5;
        double lon = 127.0;

        Course mockCourse1 = mock(Course.class);
        Course mockCourse2 = mock(Course.class);
        List<Course> mockCourses = List.of(mockCourse1, mockCourse2);

        CourseCardResponseDto mockDto1 = mock(CourseCardResponseDto.class);
        CourseCardResponseDto mockDto2 = mock(CourseCardResponseDto.class);
        List<CourseCardResponseDto> expectedDtos = List.of(mockDto1, mockDto2);

        given(courseFindService.getNearbyCourses(page, size, sort, lat, lon)).willReturn(mockCourses);
        given(courseCardDtoConverter.convert(mockCourses)).willReturn(expectedDtos);

        // when
        List<CourseCardResponseDto> result = courseViewFacade.getRecommendedCourses(page, size, sort, lat, lon);

        // then
        assertThat(result).isEqualTo(expectedDtos);
        assertThat(result).hasSize(2);

        // verify
        verify(courseFindService).getNearbyCourses(page, size, sort, lat, lon);
        verify(courseCardDtoConverter).convert(mockCourses);
    }

    @Test
    @DisplayName("getRecommendedCourses: 주변 코스가 없으면 빈 리스트를 반환한다")
    void get_recommended_courses_when_no_courses_found() {

        // given
        int page = 0;
        int size = 10;
        SortType sort = SortType.SCRAPS;
        double lat = 37.5;
        double lon = 127.0;

        List<Course> emptyCourses = Collections.emptyList();
        List<CourseCardResponseDto> emptyDtos = Collections.emptyList();

        given(courseFindService.getNearbyCourses(page, size, sort, lat, lon)).willReturn(emptyCourses);
        given(courseCardDtoConverter.convert(emptyCourses)).willReturn(emptyDtos);

        // when
        List<CourseCardResponseDto> result = courseViewFacade.getRecommendedCourses(page, size, sort, lat, lon);

        // then
        assertThat(result).isEmpty();

        // verify
        verify(courseFindService).getNearbyCourses(page, size, sort, lat, lon);
        verify(courseCardDtoConverter).convert(emptyCourses);
    }

    @Test
    @DisplayName("getRecentCourses: 최근 코스를 조회하여 DTO 리스트로 변환한다")
    void get_recent_courses() {

        // given
        int page = 0;
        int size = 10;

        Pageable mockPageable = mock(Pageable.class);
        Member mockMember = mock(Member.class);

        Course mockCourse1 = mock(Course.class);
        Course mockCourse2 = mock(Course.class);
        List<Course> mockCourses = List.of(mockCourse1, mockCourse2);

        CourseCardWithCommentDto mockDto1 = mock(CourseCardWithCommentDto.class);
        CourseCardWithCommentDto mockDto2 = mock(CourseCardWithCommentDto.class);
        List<CourseCardWithCommentDto> expectedDtos = List.of(mockDto1, mockDto2);

        mockedSortUtils.when(() -> SortUtils.resolveCourseSort(page, size, SortType.RECENT)).thenReturn(mockPageable);
        given(courseFindService.getCourses(mockPageable)).willReturn(mockCourses);
        mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(mockMember);
        given(courseCardDtoConverter.convertWithComment(mockMember, mockCourses)).willReturn(expectedDtos);

        // when
        List<CourseCardWithCommentDto> result = courseViewFacade.getRecentCourses(page, size);

        // then
        mockedSortUtils.verify(() -> SortUtils.resolveCourseSort(page, size, SortType.RECENT));
        verify(courseFindService).getCourses(mockPageable);
        mockedSecurity.verify(SecurityUtils::getCurrentMember);
        verify(courseCardDtoConverter).convertWithComment(mockMember, mockCourses);

        assertThat(result).isSameAs(expectedDtos);
        assertThat(result).hasSize(2);
        assertThat(result).isEqualTo(expectedDtos);
    }

    @Test
    @DisplayName("getRecentCourses: 최근 코스가 없으면 빈 리스트를 반환한다")
    void get_recent_courses_when_no_courses_found() {

        // given
        int page = 0;
        int size = 10;

        Pageable mockPageable = mock(Pageable.class);
        Member mockMember = mock(Member.class);
        List<Course> emptyCourses = Collections.emptyList();
        List<CourseCardWithCommentDto> emptyDtos = Collections.emptyList();

        mockedSortUtils.when(() -> SortUtils.resolveCourseSort(page, size, SortType.RECENT)).thenReturn(mockPageable);
        given(courseFindService.getCourses(mockPageable)).willReturn(emptyCourses);
        mockedSortUtils.when(SecurityUtils::getCurrentMember).thenReturn(mockMember);
        given(courseCardDtoConverter.convertWithComment(mockMember, emptyCourses)).willReturn(emptyDtos);

        // when
        List<CourseCardWithCommentDto> result = courseViewFacade.getRecentCourses(page, size);

        // then
        assertThat(result).isEmpty();

        mockedSortUtils.verify(() -> SortUtils.resolveCourseSort(page, size, SortType.RECENT));
        verify(courseFindService).getCourses(mockPageable);
        mockedSecurity.verify(SecurityUtils::getCurrentMember);
        verify(courseCardDtoConverter).convertWithComment(mockMember, emptyCourses);
    }

    @Test
    @DisplayName("getFilteredCourses: 검색 결과가 있을 때 필터와 DTO 리스트를 반환한다")
    void get_filtered_courses() {

        // given
        int page = 0;
        int size = 10;
        Integer rating = 4;
        List<String> keyword = List.of("카페");
        LocalDate startDate = LocalDate.of(2025, 11, 1);
        LocalDate endDate = LocalDate.of(2025, 11, 30);
        String with = "친구";
        String region = "서울";
        List<String> placeCode = List.of("FD6");
        List<String> tag = List.of("감성");

        Pageable mockPageable = mock(Pageable.class);
        Member mockMember = mock(Member.class);

        Course mockCourse1 = mock(Course.class);
        List<Course> courseList = List.of(mockCourse1);
        Page<Course> coursePage = new PageImpl<>(courseList, mockPageable, 1);

        CourseCardWithCommentDto mockDto = mock(CourseCardWithCommentDto.class);
        List<CourseCardWithCommentDto> expectedDtos = List.of(mockDto);

        mockedSortUtils.when(() -> SortUtils.resolveCourseSort(page, size, SortType.RECENT)).thenReturn(mockPageable);
        given(courseSearchService.searchPublicCourses(
                rating, keyword, startDate, endDate, with, region, placeCode, tag, mockPageable
        )).willReturn(coursePage);


        mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(mockMember);
        given(courseCardDtoConverter.convertWithComment(mockMember, courseList)).willReturn(expectedDtos);

        // when
        CourseSearchWithCommentDto result = courseViewFacade.getFilteredCourses(
                page, size, rating, keyword, startDate, endDate, with, region, placeCode, tag
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.filteredCourses()).isSameAs(expectedDtos);

        SearchFilterDto filterDto = result.selectedFilters();
        assertThat(filterDto).isNotNull();
        assertThat(filterDto.keyword()).isEqualTo(keyword);
        assertThat(filterDto.rating()).isEqualTo(rating);
        assertThat(filterDto.region()).isEqualTo(region);

        mockedSortUtils.verify(() -> SortUtils.resolveCourseSort(page, size, SortType.RECENT));
        verify(courseSearchService).searchPublicCourses(
                rating, keyword, startDate, endDate, with, region, placeCode, tag, mockPageable
        );
        mockedSecurity.verify(SecurityUtils::getCurrentMember);
        verify(courseCardDtoConverter).convertWithComment(mockMember, courseList);
    }

    @Test
    @DisplayName("getFilteredCourses: 검색 결과가 없으면 필터와 빈 DTO 리스트를 반환한다")
    void get_filtered_courses_when_no_content() {

        // given
        int page = 0;
        int size = 10;

        Pageable mockPageable = mock(Pageable.class);
        Page<Course> emptyPage = new PageImpl<>(Collections.emptyList(), mockPageable, 0);

        mockedSortUtils.when(() -> SortUtils.resolveCourseSort(page, size, SortType.RECENT)).thenReturn(mockPageable);
        given(courseSearchService.searchPublicCourses(
                null, null, null, null, null, null, null, null, mockPageable
        )).willReturn(emptyPage);

        // when
        CourseSearchWithCommentDto result = courseViewFacade.getFilteredCourses(
                page, size, null, null, null, null, null, null, null, null
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.filteredCourses()).isEmpty();

        SearchFilterDto filterDto = result.selectedFilters();
        assertThat(filterDto).isNotNull();
        assertThat(filterDto.keyword()).isNull();
        assertThat(filterDto.rating()).isNull();

        mockedSortUtils.verify(() -> SortUtils.resolveCourseSort(page, size, SortType.RECENT));
        verify(courseSearchService).searchPublicCourses(
                null, null, null, null, null, null, null, null, mockPageable
        );

        mockedSecurity.verifyNoInteractions();
        verify(courseCardDtoConverter, never()).convertWithComment(any(), anyList());
    }
}