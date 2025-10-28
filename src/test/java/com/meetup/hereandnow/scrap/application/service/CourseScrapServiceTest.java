package com.meetup.hereandnow.scrap.application.service;

import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.scrap.domain.CourseScrap;
import com.meetup.hereandnow.scrap.dto.response.ScrapResponseDto;
import com.meetup.hereandnow.scrap.repository.CourseScrapRepository;
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
class CourseScrapServiceTest {

    @Mock
    private CourseScrapRepository courseScrapRepository;
    @Mock
    private CourseRepository courseRepository;
    @InjectMocks
    private CourseScrapService courseScrapService;

    @Test
    @DisplayName("findOptional 호출 시 스크랩이 존재하면 CourseScrap을 반환한다")
    void findOptional_when_scrap_exists_then_return() {
        // given
        var member = mock(Member.class);
        Long memberId = 1L;
        Long courseId = 10L;
        var courseScrap = mock(CourseScrap.class);

        given(member.getId()).willReturn(memberId);
        given(courseScrapRepository.findByMemberIdAndCourseId(memberId, courseId))
                .willReturn(Optional.of(courseScrap));

        // when
        Optional<CourseScrap> result = courseScrapService.findOptional(member, courseId);

        // then
        assertThat(result).isPresent().contains(courseScrap);
    }

    @Test
    @DisplayName("스크랩 시 코스가 존재하면 카운트를 증가시키고 스크랩을 저장한다")
    void scrap_when_course_exists_then_increment_count_and_save_scrap() {
        // given
        var member = mock(Member.class);
        Long courseId = 10L;

        Course course = Course.builder().id(courseId).build();
        assertThat(course.getScrapCount()).isZero();

        given(courseRepository.findByIdWithLock(courseId)).willReturn(Optional.of(course));

        // when
        ScrapResponseDto responseDto = courseScrapService.scrap(member, courseId);

        // then
        assertThat(responseDto).isNotNull();
        then(courseRepository).should().findByIdWithLock(courseId);

        // then
        assertThat(course.getScrapCount()).isEqualTo(1);
        then(courseScrapRepository).should().save(any(CourseScrap.class));
    }

    @Test
    @DisplayName("스크랩 시 코스가 존재하지 않으면 예외를 발생시킨다")
    void scrap_when_course_does_not_exist_then_throw_exception() {
        // given
        var member = mock(Member.class);
        Long courseId = 10L;

        given(courseRepository.findByIdWithLock(courseId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> courseScrapService.scrap(member, courseId))
                .isInstanceOf(DomainException.class);
        then(courseRepository).should().findByIdWithLock(courseId);
        then(courseScrapRepository).should(never()).save(any(CourseScrap.class));
    }

    @Test
    @DisplayName("스크랩 삭제 시 코스가 존재하면 카운트를 감소시키고 스크랩을 삭제한다")
    void deleteScrap_when_course_exists_then_decrement_count_and_delete_scrap() {
        // given
        Long courseId = 10L;

        Course course = Course.builder().id(courseId).scrapCount(2).build();
        assertThat(course.getScrapCount()).isEqualTo(2);

        CourseScrap courseScrap = mock(CourseScrap.class);
        Course mockCourse = mock(Course.class);

        given(courseScrap.getCourse()).willReturn(mockCourse);
        given(mockCourse.getId()).willReturn(courseId);

        given(courseRepository.findByIdWithLock(courseId)).willReturn(Optional.of(course));

        // when
        ScrapResponseDto responseDto = courseScrapService.deleteScrap(courseScrap);

        // then
        assertThat(responseDto).isNotNull();
        then(courseRepository).should().findByIdWithLock(courseId);
        assertThat(course.getScrapCount()).isEqualTo(1);
        then(courseScrapRepository).should().delete(courseScrap);
    }

    @Test
    @DisplayName("스크랩 삭제 시 코스가 존재하지 않으면 예외를 발생시킨다")
    void deleteScrap_when_course_does_not_exist_then_throw_exception() {
        // given
        Long courseId = 10L;
        Course course = mock(Course.class);
        CourseScrap courseScrap = mock(CourseScrap.class);

        given(courseScrap.getCourse()).willReturn(course);
        given(course.getId()).willReturn(courseId);
        given(courseRepository.findByIdWithLock(courseId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> courseScrapService.deleteScrap(courseScrap))
                .isInstanceOf(DomainException.class);

        then(courseRepository).should().findByIdWithLock(courseId);
        then(course).should(never()).decrementScrapCount();
        then(courseScrapRepository).should(never()).delete(courseScrap);
    }
}
