package com.meetup.hereandnow.scrap.application.service;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.scrap.domain.CourseScrap;
import com.meetup.hereandnow.scrap.dto.response.ScrapResponseDto;
import com.meetup.hereandnow.scrap.exception.ScrapErrorCode;
import com.meetup.hereandnow.scrap.infrastructure.repository.CourseScrapRepository;
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
class CourseScrapServiceTest {

    @Mock
    private CourseScrapRepository courseScrapRepository;
    @Mock
    private CourseRepository courseRepository;
    @InjectMocks
    private CourseScrapService courseScrapService;

    private MockedStatic<ScrapResponseDto> mockedScrapDto;

    private Member mockMember;
    private Course mockCourse;

    @BeforeEach
    void set_up() {
        mockMember = mock(Member.class);
        mockCourse = mock(Course.class);
        mockedScrapDto = mockStatic(ScrapResponseDto.class);
    }

    @AfterEach
    void tear_down() {
        mockedScrapDto.close();
    }

    @Test
    @DisplayName("스크랩 생성: 기존 스크랩이 없을 때 스크랩을 생성하고 DTO를 반환한다")
    void toggle_scrap_course_creates_scrap_when_not_exists() {

        // given
        Long courseId = 1L;
        Long memberId = 10L;
        ScrapResponseDto mockResponse = mock(ScrapResponseDto.class);

        given(courseRepository.findByIdWithLock(courseId)).willReturn(Optional.of(mockCourse));
        given(mockMember.getId()).willReturn(memberId);
        given(courseScrapRepository.findByMemberIdAndCourseId(memberId, courseId)).willReturn(Optional.empty());

        mockedScrapDto.when(() -> ScrapResponseDto.from(any(CourseScrap.class))).thenReturn(mockResponse);

        // when
        ScrapResponseDto result = courseScrapService.toggleScrapCourse(mockMember, courseId);

        // then
        assertThat(result).isEqualTo(mockResponse);

        verify(mockCourse).incrementScrapCount();
        verify(courseScrapRepository).save(any(CourseScrap.class));

        verify(mockCourse, never()).decrementScrapCount();
        verify(courseScrapRepository, never()).delete(any());
    }

    @Test
    @DisplayName("스크랩 삭제: 기존 스크랩이 있을 때 스크랩을 삭제하고 DTO를 반환한다")
    void toggle_scrap_course_deletes_scrap_when_exists() {

        // given
        Long courseId = 1L;
        Long memberId = 10L;
        CourseScrap mockScrap = mock(CourseScrap.class);
        ScrapResponseDto mockResponse = mock(ScrapResponseDto.class);

        given(courseRepository.findByIdWithLock(courseId)).willReturn(Optional.of(mockCourse));
        given(mockMember.getId()).willReturn(memberId);
        given(courseScrapRepository.findByMemberIdAndCourseId(memberId, courseId)).willReturn(Optional.of(mockScrap));
        mockedScrapDto.when(ScrapResponseDto::from).thenReturn(mockResponse);

        // when
        ScrapResponseDto result = courseScrapService.toggleScrapCourse(mockMember, courseId);

        // then
        assertThat(result).isEqualTo(mockResponse);

        verify(mockCourse).decrementScrapCount();
        verify(courseScrapRepository).delete(mockScrap);

        verify(mockCourse, never()).incrementScrapCount();
        verify(courseScrapRepository, never()).save(any());
    }

    @Test
    @DisplayName("스크랩 토글 실패: 코스가 존재하지 않으면 예외를 던진다")
    void toggle_scrap_course_throws_exception_when_course_not_found() {

        // given
        Long courseId = 99L;
        given(courseRepository.findByIdWithLock(courseId)).willReturn(Optional.empty());
        Class<? extends RuntimeException> expectedException = ScrapErrorCode.COURSE_NOT_FOUND.toException().getClass();

        // when, then
        assertThatThrownBy(() -> courseScrapService.toggleScrapCourse(mockMember, courseId))
                .isInstanceOf(expectedException);

        verify(mockMember, never()).getId();

        verify(courseScrapRepository, never()).findByMemberIdAndCourseId(any(), any());
        verify(courseScrapRepository, never()).save(any());
        verify(courseScrapRepository, never()).delete(any());

        verify(mockCourse, never()).incrementScrapCount();
        verify(mockCourse, never()).decrementScrapCount();
    }

    @Test
    @DisplayName("멤버별 스크랩 조회: 레포지토리의 조회 메서드를 올바르게 호출한다")
    void get_scraps_by_member_calls_repository_correctly() {

        // given
        Pageable mockPageable = mock(Pageable.class);
        Page<CourseScrap> mockPage = new PageImpl<>(List.of(mock(CourseScrap.class)));

        given(courseScrapRepository.findScrapsByMemberWithSort(mockMember, mockPageable)).willReturn(mockPage);

        // when
        Page<CourseScrap> result = courseScrapService.getScrapsByMember(mockMember, mockPageable);

        // then
        assertThat(result).isEqualTo(mockPage);
        verify(courseScrapRepository).findScrapsByMemberWithSort(mockMember, mockPageable);
    }
}