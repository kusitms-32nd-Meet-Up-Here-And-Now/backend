package com.meetup.hereandnow.course.application.service.delete;

import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CourseDeleteServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseDeleteService courseDeleteService;

    private MockedStatic<SecurityUtils> mockedSecurity;
    private Member member;

    private static final Long TEST_COURSE_ID = 1L;
    private static final Long TEST_MEMBER_ID = 1L;

    @BeforeEach
    void setUp() {
        member = Member.builder().id(TEST_MEMBER_ID).username("user").build();

        mockedSecurity = mockStatic(SecurityUtils.class);
        when(SecurityUtils.getCurrentMember()).thenReturn(member);
    }

    @AfterEach
    void tearDown() {
        mockedSecurity.close();
    }

    @Nested
    @DisplayName("코스 식별자로 코스 삭제 테스트")
    class CourseDeleteById {

        @Test
        @DisplayName("코스 식별자로 코스 삭제를 성공한다.")
        void success_delete_course_by_course_id() {
            // given
            Course course = Course.builder().id(TEST_COURSE_ID).member(member).build();

            given(courseRepository.findById(TEST_COURSE_ID)).willReturn(Optional.of(course));

            // when
            courseDeleteService.courseDeleteById(1L);

            // then
            verify(courseRepository, times(1)).delete(course);
        }

        @Test
        @DisplayName("존재하지 않는 코스의 경우 삭제에 실패한다.")
        void fail_not_found_course() {
            // given
            given(courseRepository.findById(TEST_COURSE_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> courseDeleteService.courseDeleteById(TEST_COURSE_ID))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(CourseErrorCode.NOT_FOUND_COURSE.getMessage());
        }

        @Test
        @DisplayName("본인의 코스가 아닌 경우 삭제에 실패한다.")
        void fail_is_not_your_course() {
            // given
            Member wrongMember = Member.builder().id(2L).build();
            Course course = Course.builder().id(TEST_COURSE_ID).member(wrongMember).build();

            given(courseRepository.findById(TEST_COURSE_ID)).willReturn(Optional.of(course));

            // when & then
            assertThatThrownBy(() -> courseDeleteService.courseDeleteById(TEST_COURSE_ID))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(CourseErrorCode.IS_NOT_YOURS.getMessage());
        }
    }

}