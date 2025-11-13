package com.meetup.hereandnow.connect.application;

import com.meetup.hereandnow.connect.dto.response.CoupleRecentArchiveReseponseDto;
import com.meetup.hereandnow.connect.exception.CoupleErrorCode;
import com.meetup.hereandnow.connect.repository.CoupleCourseCommentRepository;
import com.meetup.hereandnow.connect.repository.CoupleRepository;
import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseCommentRepository;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoupleConnectingSearchServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CoupleRepository coupleRepository;

    @Mock
    private CourseCommentRepository courseCommentRepository;

    @Mock
    private CoupleCourseCommentRepository coupleCourseCommentRepository;

    @InjectMocks
    private CoupleConnectingSearchService coupleConnectingSearchService;

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

    @Test
    void success_getRecentArchive_코스가_존재하는_경우() {
        // given
        when(coupleRepository.existsByMember(mockedMember)).thenReturn(true);

        PinImage pinImage1 = mock(PinImage.class);
        PinImage pinImage2 = mock(PinImage.class);
        PinImage pinImage3 = mock(PinImage.class);
        when(pinImage1.getImageUrl()).thenReturn("image1.jpg");
        when(pinImage2.getImageUrl()).thenReturn("image2.jpg");
        when(pinImage3.getImageUrl()).thenReturn("image3.jpg");

        Pin pin = mock(Pin.class);
        when(pin.getPinImages()).thenReturn(List.of(pinImage1, pinImage2, pinImage3));

        Course course = mock(Course.class);
        when(course.getPinList()).thenReturn(List.of(pin));

        when(courseRepository.findLatestCourse(mockedMember, "연인")).thenReturn(Optional.of(course));
        when(courseCommentRepository.countByCourse(course)).thenReturn(5);
        when(coupleCourseCommentRepository.countByCourse(course)).thenReturn(3);

        // when
        CoupleRecentArchiveReseponseDto result = coupleConnectingSearchService.getRecentArchive();
        
        assertThat(result).isNotNull();
        verify(coupleRepository).existsByMember(mockedMember);
        verify(courseRepository).findLatestCourse(mockedMember, "연인");
        verify(courseCommentRepository).countByCourse(course);
        verify(coupleCourseCommentRepository).countByCourse(course);
    }

    @Test
    void success_getRecentArchive_코스가_존재하지_않는_경우() {
        // given
        when(coupleRepository.existsByMember(mockedMember)).thenReturn(true);
        when(courseRepository.findLatestCourse(mockedMember, "연인")).thenReturn(Optional.empty());

        // when
        CoupleRecentArchiveReseponseDto result = coupleConnectingSearchService.getRecentArchive();

        // then
        assertThat(result).isNull();
        verify(coupleRepository).existsByMember(mockedMember);
        verify(courseRepository).findLatestCourse(mockedMember, "연인");
        verifyNoInteractions(courseCommentRepository, coupleCourseCommentRepository);
    }

    @Test
    void fail_getRecentArchive_커플이_아닌_경우() {
        // given
        when(coupleRepository.existsByMember(mockedMember)).thenReturn(false);

        assertThatThrownBy(() -> coupleConnectingSearchService.getRecentArchive())
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(CoupleErrorCode.NOT_FOUND_COUPLE.getMessage());


        verify(coupleRepository).existsByMember(mockedMember);
        verifyNoInteractions(courseRepository, courseCommentRepository, coupleCourseCommentRepository);
    }

    @Test
    void success_getCommentCount_댓글_개수_합산() {
        // given
        when(coupleRepository.existsByMember(mockedMember)).thenReturn(true);

        Course course = mock(Course.class);
        when(course.getPinList()).thenReturn(new ArrayList<>());

        when(courseRepository.findLatestCourse(mockedMember, "연인")).thenReturn(Optional.of(course));
        when(courseCommentRepository.countByCourse(course)).thenReturn(10);
        when(coupleCourseCommentRepository.countByCourse(course)).thenReturn(7);

        // when
        coupleConnectingSearchService.getRecentArchive();

        // then
        verify(courseCommentRepository).countByCourse(course);
        verify(coupleCourseCommentRepository).countByCourse(course);
    }
}
