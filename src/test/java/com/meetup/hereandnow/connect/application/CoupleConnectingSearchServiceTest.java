package com.meetup.hereandnow.connect.application;

import com.meetup.hereandnow.connect.domain.vo.CourseSearchCriteria;
import com.meetup.hereandnow.connect.dto.response.CoupleCourseSearchResponseDto;
import com.meetup.hereandnow.connect.dto.response.CoupleRecentArchiveReseponseDto;
import com.meetup.hereandnow.connect.infrastructure.aggregator.CommentCountAggregator;
import com.meetup.hereandnow.connect.infrastructure.builder.CoupleSpecificationBuilder;
import com.meetup.hereandnow.connect.infrastructure.strategy.CourseImageSelector;
import com.meetup.hereandnow.connect.infrastructure.validator.CoupleValidator;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoupleConnectingSearchServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CoupleValidator coupleValidator;

    @Mock
    private CourseImageSelector imageSelector;

    @Mock
    private CommentCountAggregator commentCountAggregator;

    @Mock
    private CoupleSpecificationBuilder specificationBuilder;

    @InjectMocks
    private CoupleConnectingSearchService service;

    private MockedStatic<SecurityUtils> mockedSecurity;
    private Member mockedMember;

    @BeforeEach
    void setUp() {
        mockedSecurity = Mockito.mockStatic(SecurityUtils.class);
        mockedMember = Member.builder().id(1L).build();
        when(SecurityUtils.getCurrentMember()).thenReturn(mockedMember);
    }

    @AfterEach
    void tearDown() {
        mockedSecurity.close();
    }

    @Nested
    @DisplayName("getRecentArchive 테스트")
    class GetRecentArchive {

        @Test
        @DisplayName("가장 최근의 연인과 함께한 기록 조회에 성공한다.")
        void success_get_recent_couple_archive() {
            // given
            Course course = mock(Course.class);
            List<String> images = List.of("image1.jpg", "image2.jpg", "image3.jpg");

            when(courseRepository.findLatestCourse(any(Member.class), anyString()))
                    .thenReturn(Optional.of(course));
            when(imageSelector.selectRandomImages(course)).thenReturn(images);
            when(commentCountAggregator.aggregate(course)).thenReturn(10);

            // when
            CoupleRecentArchiveReseponseDto result = service.getRecentArchive();

            // then
            assertThat(result).isNotNull();
            verify(coupleValidator).validate(mockedMember);
            verify(courseRepository).findLatestCourse(any(Member.class), anyString());
            verify(imageSelector).selectRandomImages(course);
            verify(commentCountAggregator).aggregate(course);
        }

        @Test
        @DisplayName("success_getRecentArchive_최근_아카이브가_없으면_null_반환")
        void success_not_found_recent_archive_get_null() {
            // given
            when(courseRepository.findLatestCourse(any(Member.class), anyString()))
                    .thenReturn(Optional.empty());

            // when
            CoupleRecentArchiveReseponseDto result = service.getRecentArchive();

            // then
            assertThat(result).isNull();
            verify(coupleValidator).validate(mockedMember);
            verify(courseRepository).findLatestCourse(any(Member.class), anyString());
            verifyNoInteractions(imageSelector, commentCountAggregator);
        }
    }

    @Nested
    @DisplayName("getCourseFolder 테스트")
    class GetCourseFolder{

        @Test
        @DisplayName("코스 폴더 조회에 성공한다.")
        void success_get_course_folder() {
            // given
            Course course1 = mock(Course.class);
            Course course2 = mock(Course.class);
            List<Course> courses = List.of(course1, course2);
            Page<Course> coursePage = new PageImpl<>(courses);

            when(specificationBuilder.build(any(Member.class), any(CourseSearchCriteria.class)))
                    .thenReturn(mock(Specification.class));
            when(courseRepository.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(coursePage);
            when(commentCountAggregator.aggregate(any(Course.class))).thenReturn(5);

            // when
            CoupleCourseSearchResponseDto result = service.getCourseFolder(
                    0, 10, 5, List.of("카페"),
                    LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31),
                    "서울", List.of("CAFE"), List.of("데이트")
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.filteredCourses()).hasSize(2);
            verify(specificationBuilder).build(any(Member.class), any(CourseSearchCriteria.class));
            verify(courseRepository).findAll(any(Specification.class), any(PageRequest.class));
            verify(commentCountAggregator, times(2)).aggregate(any(Course.class));
        }

        @Test
        @DisplayName("검색 결과가 없는 경우 빈 결과를 반환한다.")
        void success_return_null_if_not_found() {
            // given
            Page<Course> emptyPage = new PageImpl<>(Collections.emptyList());

            when(specificationBuilder.build(any(Member.class), any(CourseSearchCriteria.class)))
                    .thenReturn(mock(Specification.class));
            when(courseRepository.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(emptyPage);

            // when
            CoupleCourseSearchResponseDto result = service.getCourseFolder(
                    0, 10, null, null, null, null, null, null, null
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.filteredCourses()).isEmpty();
            verify(specificationBuilder).build(any(Member.class), any(CourseSearchCriteria.class));
            verify(courseRepository).findAll(any(Specification.class), any(PageRequest.class));
            verifyNoInteractions(commentCountAggregator);
        }

        @Test
        @DisplayName("모든 필터 적용에 성공한다.")
        void success_all_filter_() {
            // given
            Page<Course> coursePage = new PageImpl<>(List.of(mock(Course.class)));

            when(specificationBuilder.build(any(Member.class), any(CourseSearchCriteria.class)))
                    .thenReturn(mock(Specification.class));
            when(courseRepository.findAll(any(Specification.class), any(PageRequest.class)))
                    .thenReturn(coursePage);
            when(commentCountAggregator.aggregate(any(Course.class))).thenReturn(3);

            // when
            CoupleCourseSearchResponseDto result = service.getCourseFolder(
                    0, 20,
                    5,
                    List.of("카페", "맛집"),
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 12, 31),
                    "서울",
                    List.of("CAFE", "RESTAURANT"),
                    List.of("데이트", "힐링")
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.selectedFilters()).isNotNull();
            assertThat(result.selectedFilters().rating()).isEqualTo(5);
            assertThat(result.selectedFilters().keyword()).containsExactly("카페", "맛집");
            assertThat(result.selectedFilters().region()).isEqualTo("서울");
        }
    }
}

