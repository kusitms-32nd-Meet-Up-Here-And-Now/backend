package com.meetup.hereandnow.archive.application.service;

import com.meetup.hereandnow.archive.application.service.converter.CourseCardDtoConverterService;
import com.meetup.hereandnow.archive.dto.response.CourseCardDto;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.scrap.domain.CourseScrap;
import com.meetup.hereandnow.scrap.infrastructure.repository.CourseScrapRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ArchiveCourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseScrapRepository courseScrapRepository;

    @Mock
    private CourseCardDtoConverterService converterService;

    @InjectMocks
    private ArchiveCourseService archiveCourseService;


    @Test
    @DisplayName("내가 스크랩한 코스 조회 시 스크랩이 존재하면 DTO 리스트로 변환하여 반환한다")
    void get_my_scrapped_courses() {
        // given
        Member member = mock(Member.class);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Course course1 = mock(Course.class);
        Course course2 = mock(Course.class);
        CourseScrap scrap1 = mock(CourseScrap.class);
        CourseScrap scrap2 = mock(CourseScrap.class);

        given(scrap1.getCourse()).willReturn(course1);
        given(scrap2.getCourse()).willReturn(course2);

        Page<CourseScrap> scrapPage = new PageImpl<>(List.of(scrap1, scrap2));
        given(courseScrapRepository.findByMemberWithCourse(member, pageRequest)).willReturn(scrapPage);

        List<CourseCardDto> expectedList = List.of(mock(CourseCardDto.class));
        given(converterService.convertToCourseCardDto(List.of(course1, course2))).willReturn(expectedList);

        // when
        List<CourseCardDto> resultList = archiveCourseService.getMyScrappedCourses(member, pageRequest);

        // then
        assertThat(resultList).isEqualTo(expectedList);
        then(courseScrapRepository).should().findByMemberWithCourse(member, pageRequest);
        then(converterService).should().convertToCourseCardDto(List.of(course1, course2));
    }

    @Test
    @DisplayName("내가 스크랩한 코스 조회 시 스크랩이 존재하지 않으면 빈 리스트를 반환한다")
    void get_my_scrapped_courses_no_scrap() {
        // given
        Member member = mock(Member.class);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<CourseScrap> emptyPage = Page.empty();
        given(courseScrapRepository.findByMemberWithCourse(member, pageRequest)).willReturn(emptyPage);

        // when
        List<CourseCardDto> resultList = archiveCourseService.getMyScrappedCourses(member, pageRequest);

        // then
        assertThat(resultList).isEmpty();
        then(courseScrapRepository).should().findByMemberWithCourse(member, pageRequest);
        then(converterService).should(never()).convertToCourseCardDto(any());
    }

    @Test
    @DisplayName("내가 생성한 코스 조회 시 생성한 코스가 존재하면 DTO 리스트로 변환하여 반환한다")
    void get_my_created_courses() {
        // given
        Member member = mock(Member.class);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Course course1 = mock(Course.class);
        Course course2 = mock(Course.class);

        Page<Course> coursePage = new PageImpl<>(List.of(course1, course2));
        given(courseRepository.findByMemberOrderByCreatedAtDesc(member, pageRequest)).willReturn(coursePage);

        List<CourseCardDto> expectedList = List.of(mock(CourseCardDto.class));
        given(converterService.convertToCourseCardDto(List.of(course1, course2))).willReturn(expectedList);

        // when
        List<CourseCardDto> resultList = archiveCourseService.getMyCreatedCourses(member, pageRequest);

        // then
        assertThat(resultList).isEqualTo(expectedList);
        then(courseRepository).should().findByMemberOrderByCreatedAtDesc(member, pageRequest);
        then(converterService).should().convertToCourseCardDto(List.of(course1, course2));
    }

    @Test
    @DisplayName("내가 생성한 코스 조회 시 생성한 코스가 존재하지 않으면 빈 리스트를 반환한다")
    void get_my_created_courses_no_created_course() {
        // given
        Member member = mock(Member.class);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Course> emptyPage = Page.empty();
        given(courseRepository.findByMemberOrderByCreatedAtDesc(member, pageRequest)).willReturn(emptyPage);

        // when
        List<CourseCardDto> resultList = archiveCourseService.getMyCreatedCourses(member, pageRequest);

        // then
        assertThat(resultList).isEmpty();
        then(courseRepository).should().findByMemberOrderByCreatedAtDesc(member, pageRequest);
        then(converterService).should(never()).convertToCourseCardDto(any());
    }
}
