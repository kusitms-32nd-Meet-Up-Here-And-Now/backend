package com.meetup.hereandnow.course.application.service.search;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CourseSearchServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseSearchService courseSearchService;

    private Member testMember;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        testMember = Member.builder()
                .id(1L)
                .nickname("testUser")
                .build();
        pageable = PageRequest.of(0, 32, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Test
    @DisplayName("기본 검색 (멤버와 페이징만) 시 올바르게 호출된다")
    void searchCourses_withOnlyMemberAndPageable_shouldCallRepository() {
        // given
        Page<Course> expectedPage = new PageImpl<>(List.of());
        given(courseRepository.findAll(any(Specification.class), eq(pageable))).willReturn(expectedPage);

        // when
        Page<Course> result = courseSearchService.searchCourses(
                testMember, null, null, null, null, null, null, pageable
        );

        // then
        assertThat(result).isEqualTo(expectedPage);
        verify(courseRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("모든 검색 필터 적용 시 올바르게 호출된다")
    void searchCourses_withAllFilters_shouldCallRepository() {
        // given
        Integer rating = 4;
        List<String> keywords = List.of("키워드1", "키워드2");
        LocalDate date = LocalDate.of(2024, 1, 1);
        String with = "친구";
        String region = "강남";
        List<String> tags = List.of("태그1", "태그2");

        Page<Course> expectedPage = new PageImpl<>(Collections.emptyList());
        given(courseRepository.findAll(any(Specification.class), eq(pageable)))
                .willReturn(expectedPage);

        // when
        Page<Course> result = courseSearchService.searchCourses(
                testMember, rating, keywords, date, with, region, tags, pageable
        );

        // then
        assertThat(result).isEqualTo(expectedPage);
        verify(courseRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("빈 값/null 필터는 무시된다")
    void searchCourses_withEmptyOrBlankFilters_shouldIgnoreFilters() {
        // given
        Integer rating = 0;
        List<String> keywords = Collections.emptyList();
        String with = " ";
        String region = null;
        List<String> tags = null;

        Page<Course> expectedPage = new PageImpl<>(Collections.emptyList());
        given(courseRepository.findAll(any(Specification.class), eq(pageable)))
                .willReturn(expectedPage);

        // when
        Page<Course> result = courseSearchService.searchCourses(
                testMember, rating, keywords, null, with, region, tags, pageable
        );

        // then
        assertThat(result).isEqualTo(expectedPage);
        verify(courseRepository).findAll(any(Specification.class), eq(pageable));
    }
}