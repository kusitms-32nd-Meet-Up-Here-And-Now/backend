package com.meetup.hereandnow.course.application.service.view;

import com.meetup.hereandnow.core.infrastructure.value.SortType;
import com.meetup.hereandnow.core.util.SortUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseFindServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseFindService courseFindService;

    private MockedStatic<SortUtils> mockedSortUtils;

    @BeforeEach
    void setUp() {
        mockedSortUtils = mockStatic(SortUtils.class);
    }

    @AfterEach
    void tearDown() {
        mockedSortUtils.close();
    }

    @Test
    @DisplayName("getNearbyCourses: 리뷰순 정렬 시 전용 레포지토리 메서드를 호출하고 id 순서대로 정렬된 결과를 반환한다")
    void get_nearby_courses_sorted_by_reviews() {

        // given
        int page = 0;
        int size = 10;
        SortType sort = SortType.REVIEWS;
        double lat = 37.5;
        double lon = 127.0;

        List<Long> sortedIds = List.of(2L, 1L);
        Page<Long> mockIdPage = new PageImpl<>(sortedIds);

        Course course1 = mock(Course.class);
        given(course1.getId()).willReturn(1L);
        Course course2 = mock(Course.class);
        given(course2.getId()).willReturn(2L);
        List<Course> unsortedCourses = List.of(course1, course2);

        given(courseRepository.findNearbyCourseIdsSortedByCommentCount(any(Point.class), any(Pageable.class)))
                .willReturn(mockIdPage);
        given(courseRepository.findCoursesWithDetailsByIds(sortedIds))
                .willReturn(unsortedCourses);

        // when
        List<Course> result = courseFindService.getNearbyCourses(page, size, sort, lat, lon);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(2L);
        assertThat(result.get(1).getId()).isEqualTo(1L);

        ArgumentCaptor<Point> pointCaptor = ArgumentCaptor.forClass(Point.class);
        verify(courseRepository).findNearbyCourseIdsSortedByCommentCount(pointCaptor.capture(), any(Pageable.class));

        Point capturedPoint = pointCaptor.getValue();
        assertThat(capturedPoint.getX()).isEqualTo(lon);
        assertThat(capturedPoint.getY()).isEqualTo(lat);

        mockedSortUtils.verifyNoInteractions();
        verify(courseRepository, never()).findNearbyCourseIds(any(), any());
    }

    @Test
    @DisplayName("getNearbyCourses: 일반 정렬 시 SortUtils를 사용하고 id 순서대로 정렬된 결과를 반환한다")
    void get_nearby_courses_sorted_by_other() {

        // given
        int page = 0;
        int size = 10;
        SortType sort = SortType.RECENT;
        double lat = 37.5;
        double lon = 127.0;

        Pageable mockPageable = PageRequest.of(page, size);

        List<Long> sortedIds = List.of(3L, 4L);
        Page<Long> mockIdPage = new PageImpl<>(sortedIds);

        Course course3 = mock(Course.class);
        given(course3.getId()).willReturn(3L);
        Course course4 = mock(Course.class);
        given(course4.getId()).willReturn(4L);
        List<Course> unsortedCourses = List.of(course4, course3);

        mockedSortUtils.when(() -> SortUtils.resolveCourseSortNQ(page, size, sort)).thenReturn(mockPageable);
        given(courseRepository.findNearbyCourseIds(any(Point.class), eq(mockPageable))).willReturn(mockIdPage);
        given(courseRepository.findCoursesWithDetailsByIds(sortedIds)).willReturn(unsortedCourses);

        // when
        List<Course> result = courseFindService.getNearbyCourses(page, size, sort, lat, lon);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(course3, course4);

        mockedSortUtils.verify(() -> SortUtils.resolveCourseSortNQ(page, size, sort));

        ArgumentCaptor<Point> pointCaptor = ArgumentCaptor.forClass(Point.class);
        verify(courseRepository).findNearbyCourseIds(pointCaptor.capture(), eq(mockPageable));

        Point capturedPoint = pointCaptor.getValue();
        assertThat(capturedPoint.getX()).isEqualTo(lon);
        assertThat(capturedPoint.getY()).isEqualTo(lat);

        verify(courseRepository, never()).findNearbyCourseIdsSortedByCommentCount(any(), any());
    }

    @Test
    @DisplayName("getNearbyCourses: 검색 결과가 없으면 빈 리스트를 반환하고 상세 조회를 수행하지 않는다")
    void get_nearby_courses_returns_empty_list() {

        // given
        int page = 0;
        int size = 10;
        SortType sort = SortType.RECENT;
        double lat = 37.5;
        double lon = 127.0;

        Pageable mockPageable = PageRequest.of(page, size);
        Page<Long> emptyPage = new PageImpl<>(Collections.emptyList());

        mockedSortUtils.when(() -> SortUtils.resolveCourseSortNQ(page, size, sort)).thenReturn(mockPageable);
        given(courseRepository.findNearbyCourseIds(any(Point.class), eq(mockPageable))).willReturn(emptyPage);

        // when
        List<Course> result = courseFindService.getNearbyCourses(page, size, sort, lat, lon);

        // then
        assertThat(result).isEmpty();
        verify(courseRepository, never()).findCoursesWithDetailsByIds(anyList());
    }

    @Test
    @DisplayName("getCourses: 코스 페이지에 내용이 있으면 리스트를 반환한다")
    void get_courses() {

        // given
        Course mockCourse1 = mock(Course.class);
        Course mockCourse2 = mock(Course.class);
        List<Course> courseList = List.of(mockCourse1, mockCourse2);

        int page = 0;
        int size = 10;
        Pageable mockPageable = PageRequest.of(page, size);
        Page<Course> mockPage = new PageImpl<>(courseList, mockPageable, courseList.size());

        given(courseRepository.findCoursesWithMember(mockPageable)).willReturn(mockPage);

        // when
        List<Course> result = courseFindService.getCourses(mockPageable);

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(mockCourse1, mockCourse2);
        verify(courseRepository).findCoursesWithMember(mockPageable);
    }

    @Test
    @DisplayName("getCourses: 코스 페이지에 내용이 없으면 빈 리스트를 반환한다")
    void get_courses_when_no_content_exists() {

        // given
        int page = 0;
        int size = 10;
        Pageable mockPageable = PageRequest.of(page, size);
        Page<Course> emptyPage = new PageImpl<>(Collections.emptyList(), mockPageable, 0);

        given(courseRepository.findCoursesWithMember(mockPageable)).willReturn(emptyPage);

        // when
        List<Course> result = courseFindService.getCourses(mockPageable);

        // then
        assertThat(result).isEmpty();
        verify(courseRepository).findCoursesWithMember(mockPageable);
    }
}