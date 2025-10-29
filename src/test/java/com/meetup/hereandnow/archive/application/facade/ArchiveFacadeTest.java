package com.meetup.hereandnow.archive.application.facade;

import com.meetup.hereandnow.archive.application.service.ArchiveCourseService;
import com.meetup.hereandnow.archive.dto.response.CourseCardDto;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class ArchiveFacadeTest {

    @Mock
    private ArchiveCourseService archiveCourseService;

    @InjectMocks
    private ArchiveFacade archiveFacade;


    @Test
    @DisplayName("내가 스크랩한 코스 조회 시 현재 멤버와 페이지 요청을 서비스에 전달하고 DTO 리스트를 반환한다")
    void get_my_scrapped_courses() {
        // given
        int page = 0;
        int size = 10;
        Member member = mock(Member.class);
        PageRequest pageRequest = PageRequest.of(page, size);
        List<CourseCardDto> expectedList = List.of(mock(CourseCardDto.class));

        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(member);

            given(archiveCourseService.getMyScrappedCourses(member, pageRequest)).willReturn(expectedList);

            // when
            List<CourseCardDto> resultList = archiveFacade.getMyScrappedCourses(page, size);

            // then
            assertThat(resultList).isEqualTo(expectedList);
            mockedSecurity.verify(SecurityUtils::getCurrentMember);
            then(archiveCourseService).should().getMyScrappedCourses(member, pageRequest);
        }
    }

    @Test
    @DisplayName("내가 생성한 코스 조회 시 현재 멤버와 페이지 요청을 서비스에 전달하고 DTO 리스트를 반환한다")
    void get_my_created_courses() {
        // given
        int page = 1;
        int size = 5;
        Member member = mock(Member.class);
        PageRequest pageRequest = PageRequest.of(page, size);
        List<CourseCardDto> expectedList = List.of(
                mock(CourseCardDto.class),
                mock(CourseCardDto.class)
        );

        try (MockedStatic<SecurityUtils> mockedSecurity = Mockito.mockStatic(SecurityUtils.class)) {
            mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(member);

            given(archiveCourseService.getMyCreatedCourses(member, pageRequest)).willReturn(expectedList);

            // when
            List<CourseCardDto> resultList = archiveFacade.getMyCreatedCourses(page, size);

            // then
            assertThat(resultList).isEqualTo(expectedList);
            mockedSecurity.verify(SecurityUtils::getCurrentMember);
            then(archiveCourseService).should().getMyCreatedCourses(member, pageRequest);
        }
    }
}
