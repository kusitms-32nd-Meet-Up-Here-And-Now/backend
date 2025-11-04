package com.meetup.hereandnow.archive.application.facade;

import com.meetup.hereandnow.archive.application.service.ArchiveCourseService;
import com.meetup.hereandnow.archive.dto.response.CourseFolderResponseDto;
import com.meetup.hereandnow.archive.dto.response.RecentArchiveResponseDto;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.member.domain.Member;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ArchiveFacadeTest {

    @Mock
    private ArchiveCourseService archiveCourseService;

    @InjectMocks
    private ArchiveFacade archiveFacade;

    private MockedStatic<SecurityUtils> mockSecurityUtils;

    private Member mockMember;

    @BeforeEach
    void setUp() {
        mockSecurityUtils = Mockito.mockStatic(SecurityUtils.class);
        mockMember = Member.builder().id(1L).nickname("testUser").build();
    }

    @AfterEach
    void tearDown() {
        mockSecurityUtils.close();
    }

    @Nested
    @DisplayName("getRecentArchive")
    class GetRecentArchive {

        @Test
        @DisplayName("최근 코스가 존재할 경우 DTO를 반환한다")
        void get_recent_archive_when_exists() {

            // given
            Course mockCourse = Course.builder().id(100L).courseTitle("최근 코스").build();
            List<String> mockImages = List.of("image1.jpg", "image2.jpg");

            mockSecurityUtils.when(SecurityUtils::getCurrentMember).thenReturn(mockMember);

            given(archiveCourseService.getRecentCourseByMember(mockMember)).willReturn(Optional.of(mockCourse));
            given(archiveCourseService.getCourseImages(mockCourse.getId())).willReturn(mockImages);

            // when
            RecentArchiveResponseDto response = archiveFacade.getRecentArchive();

            // then
            assertThat(response).isNotNull();
            then(archiveCourseService).should(times(1)).getRecentCourseByMember(mockMember);
            then(archiveCourseService).should(times(1)).getCourseImages(mockCourse.getId());
        }

        @Test
        @DisplayName("최근 코스가 존재하지 않을 경우 null을 반환한다")
        void get_recent_archive_when_not_exists() {

            // given
            mockSecurityUtils.when(SecurityUtils::getCurrentMember).thenReturn(mockMember);
            given(archiveCourseService.getRecentCourseByMember(mockMember)).willReturn(Optional.empty());

            // when
            RecentArchiveResponseDto response = archiveFacade.getRecentArchive();

            // then
            assertThat(response).isNull();
            then(archiveCourseService).should(times(1)).getRecentCourseByMember(mockMember);
            then(archiveCourseService).should(never()).getCourseImages(any());
        }
    }


    @Nested
    @DisplayName("getMyCreatedCourses")
    class GetMyCreatedCourses {

        private PageRequest pageRequest;

        @BeforeEach
        void nestedSetUp() {
            pageRequest = PageRequest.of(0, 10);
        }

        @Test
        @DisplayName("생성한 코스가 존재할 경우 DTO 리스트를 반환한다")
        void get_created_courses_when_exists() {

            // given
            mockSecurityUtils.when(SecurityUtils::getCurrentMember).thenReturn(mockMember);

            List<Long> mockIds = List.of(100L, 101L);
            Page<Long> idPage = new PageImpl<>(mockIds, pageRequest, 2);
            given(archiveCourseService.getCourseIdsByMember(mockMember, pageRequest)).willReturn(idPage);

            Course course1 = Course.builder().id(100L).build();
            Course course2 = Course.builder().id(101L).build();
            List<Course> mockCourses = List.of(course1, course2);
            given(archiveCourseService.getCoursesWithPins(mockIds)).willReturn(mockCourses);

            // when
            List<CourseFolderResponseDto> response = archiveFacade.getMyCreatedCourses(0, 10);

            // then
            assertThat(response).isNotNull();
            assertThat(response).hasSize(2);
            then(archiveCourseService).should(times(1)).getCourseIdsByMember(mockMember, pageRequest);
            then(archiveCourseService).should(times(1)).getCoursesWithPins(mockIds);
        }

        @Test
        @DisplayName("생성한 코스가 없을 경우 빈 리스트를 반환한다")
        void get_created_courses_when_not_exists() {

            // given
            mockSecurityUtils.when(SecurityUtils::getCurrentMember).thenReturn(mockMember);

            Page<Long> emptyIdPage = Page.empty(pageRequest);
            given(archiveCourseService.getCourseIdsByMember(mockMember, pageRequest)).willReturn(emptyIdPage);

            // when
            List<CourseFolderResponseDto> response = archiveFacade.getMyCreatedCourses(0, 10);

            // then
            assertThat(response).isNotNull();
            assertThat(response).isEmpty();
            then(archiveCourseService).should(times(1)).getCourseIdsByMember(mockMember, pageRequest);
            then(archiveCourseService).should(never()).getCoursesWithPins(anyList());
        }
    }
}