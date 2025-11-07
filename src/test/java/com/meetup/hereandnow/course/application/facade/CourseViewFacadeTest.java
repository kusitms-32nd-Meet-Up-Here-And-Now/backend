package com.meetup.hereandnow.course.application.facade;

import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.application.service.view.CourseDetailsViewService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.response.CourseDetailsResponseDto;
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

    @InjectMocks
    private CourseViewFacade courseViewFacade;

    private MockedStatic<SecurityUtils> mockedSecurity;

    private MockedStatic<CourseDetailsResponseDto> mockedDto;

    @BeforeEach
    void setUp() {
        mockedSecurity = Mockito.mockStatic(SecurityUtils.class);
        mockedDto = Mockito.mockStatic(CourseDetailsResponseDto.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurity.close();
        mockedDto.close();
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
}