package com.meetup.hereandnow.archive.application.service;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.infrastructure.repository.PinImageRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ArchiveCourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private PinImageRepository pinImageRepository;

    @Mock
    private ObjectStorageService objectStorageService;

    @InjectMocks
    private ArchiveCourseService archiveCourseService;

    @Nested
    @DisplayName("getRecentCourseByMember")
    class GetRecentCourseByMember {

        @Test
        @DisplayName("회원의 최근 코스가 존재하면 Optional<Course>를 반환한다")
        void get_recent_course_by_member() {

            // given
            Member member = Member.builder().id(1L).build();
            Course mockCourse = Course.builder().id(100L).member(member).build();

            given(courseRepository.findByMemberOrderByCreatedAtDesc(member.getId()))
                    .willReturn(Optional.of(mockCourse));

            // when
            Optional<Course> result = archiveCourseService.getRecentCourseByMember(member);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(mockCourse);
            then(courseRepository).should(times(1)).findByMemberOrderByCreatedAtDesc(member.getId());
        }

        @Test
        @DisplayName("회원의 최근 코스가 없으면 빈 Optional을 반환한다")
        void get_recent_course_by_member_not_found() {

            // given
            Member member = Member.builder().id(1L).build();

            given(courseRepository.findByMemberOrderByCreatedAtDesc(member.getId()))
                    .willReturn(Optional.empty());

            // when
            Optional<Course> result = archiveCourseService.getRecentCourseByMember(member);

            // then
            assertThat(result).isEmpty();
            then(courseRepository).should(times(1)).findByMemberOrderByCreatedAtDesc(member.getId());
        }
    }

    @Nested
    @DisplayName("getCourseImages")
    class GetCourseImages {

        @Test
        @DisplayName("이미지가 3개 이상이면 셔플 후 3개의 전체 URL을 반환한다")
        void get_course_images() {

            // given
            Long courseId = 1L;
            List<String> rawUrls = new ArrayList<>(List.of("img1.jpg", "img2.jpg", "img3.jpg", "img4.jpg", "img5.jpg"));

            given(pinImageRepository.findImageUrlsByCourseId(courseId)).willReturn(rawUrls);
            given(objectStorageService.buildImageUrl(anyString())).willAnswer(invocation ->
                    "https://" + invocation.getArgument(0)
            );

            // when
            List<String> resultUrls = archiveCourseService.getCourseImages(courseId);

            // then
            then(pinImageRepository).should(times(1)).findImageUrlsByCourseId(courseId);
            then(objectStorageService).should(times(3)).buildImageUrl(anyString());
            assertThat(resultUrls).hasSize(3);
            assertThat(resultUrls).allMatch(url -> url.startsWith("https://"));
        }

        @Test
        @DisplayName("이미지가 3개 미만이면 셔플 후 해당 개수만큼의 빌드된 URL을 반환한다")
        void get_course_images_less_than_3() {

            // given
            Long courseId = 1L;
            List<String> rawUrls = new ArrayList<>(List.of("img1.jpg", "img2.jpg"));

            given(pinImageRepository.findImageUrlsByCourseId(courseId)).willReturn(rawUrls);
            given(objectStorageService.buildImageUrl(anyString())).willAnswer(invocation ->
                    "https://" + invocation.getArgument(0)
            );

            // when
            List<String> resultUrls = archiveCourseService.getCourseImages(courseId);

            // then
            then(objectStorageService).should(times(2)).buildImageUrl(anyString());
            assertThat(resultUrls).hasSize(2);
        }

        @Test
        @DisplayName("이미지가 없으면 빈 리스트를 반환하고 objectStorageService를 호출하지 않는다")
        void get_course_images_not_found() {

            // given
            Long courseId = 1L;
            given(pinImageRepository.findImageUrlsByCourseId(courseId)).willReturn(Collections.emptyList());

            // when
            List<String> resultUrls = archiveCourseService.getCourseImages(courseId);

            // then
            then(objectStorageService).should(never()).buildImageUrl(anyString());
            assertThat(resultUrls).isEmpty();
        }
    }

    @Nested
    @DisplayName("getCourseIdsByMember")
    class GetCourseIdsByMember {

        @Test
        @DisplayName("레포지토리를 호출하고 Page<Long>을 그대로 반환한다")
        void get_course_ids_by_member() {

            // given
            Member member = Member.builder().id(1L).build();
            PageRequest pageRequest = PageRequest.of(0, 10);
            List<Long> ids = List.of(100L, 101L);
            Page<Long> mockIdPage = new PageImpl<>(ids, pageRequest, 2);

            given(courseRepository.findCourseIdsByMember(member, pageRequest)).willReturn(mockIdPage);

            // when
            Page<Long> resultPage = archiveCourseService.getCourseIdsByMember(member, pageRequest);

            // then
            assertThat(resultPage).isEqualTo(mockIdPage);
            then(courseRepository).should(times(1)).findCourseIdsByMember(member, pageRequest);
        }
    }

    @Nested
    @DisplayName("getCoursesWithPins")
    class GetCoursesWithPins {

        @Test
        @DisplayName("레포지토리를 호출하고 List<Course>를 그대로 반환한다")
        void get_courses_with_pins() {

            // given
            List<Long> courseIds = List.of(100L, 101L);
            List<Course> mockCourses = List.of(
                    Course.builder().id(100L).build(),
                    Course.builder().id(101L).build()
            );
            given(courseRepository.findWithPinsByIds(courseIds)).willReturn(mockCourses);

            // when
            List<Course> resultCourses = archiveCourseService.getCoursesWithPins(courseIds);

            // then
            assertThat(resultCourses).isEqualTo(mockCourses);
            then(courseRepository).should(times(1)).findWithPinsByIds(courseIds);
        }
    }
}