package com.meetup.hereandnow.course.application.service.comment;

import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.domain.entity.CourseComment;
import com.meetup.hereandnow.course.dto.request.CourseCommentSaveRequestDto;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.course.infrastructure.repository.CourseCommentRepository;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseCommentSaveServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseCommentRepository courseCommentRepository;

    @InjectMocks
    private CourseCommentSaveService courseCommentSaveService;

    private MockedStatic<SecurityUtils> mockedSecurity;
    private Member member;
    private CourseCommentSaveRequestDto courseCommentSaveRequestDto;

    @BeforeEach
    void setUp() {
        member = Member.builder().id(1L).build();

        courseCommentSaveRequestDto = new CourseCommentSaveRequestDto(1L, "여기 장소 좋아요.");

        mockedSecurity = mockStatic(SecurityUtils.class);
        when(SecurityUtils.getCurrentMember()).thenReturn(member);
    }

    @AfterEach
    void tearDown() {
        mockedSecurity.close();
    }

    @Nested
    @DisplayName("코스 댓글 저장 테스트")
    class SaveCourseCommentTest {

        @Test
        @DisplayName("정상적으로 코스 댓글을 저장한다")
        void success_course_comment_save() {
            // given
            Course course = Course.builder()
                    .id(courseCommentSaveRequestDto.courseId())
                    .build();

            when(courseRepository.findById(courseCommentSaveRequestDto.courseId())).thenReturn(Optional.of(course));

            // when
            courseCommentSaveService.saveCourseComment(courseCommentSaveRequestDto);

            // then
            ArgumentCaptor<CourseComment> courseCommentCaptor = ArgumentCaptor.forClass(CourseComment.class);
            verify(courseCommentRepository).save(courseCommentCaptor.capture());

            CourseComment savedComment = courseCommentCaptor.getValue();
            assertThat(savedComment.getCourse()).isEqualTo(course);
            assertThat(savedComment.getMember()).isEqualTo(member);
            assertThat(savedComment.getContent()).isEqualTo(courseCommentSaveRequestDto.content());
        }

        @Test
        @DisplayName("존재하지 않는 코스 ID로 요청 시 예외를 발생시킨다")
        void fail_course_not_found() {
            // given
            Long nonExistentCourseId = 999L;
            String content = "댓글 내용";
            CourseCommentSaveRequestDto requestDto = new CourseCommentSaveRequestDto(nonExistentCourseId, content);

            when(courseRepository.findById(nonExistentCourseId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> courseCommentSaveService.saveCourseComment(requestDto))
                    .isInstanceOf(CourseErrorCode.NOT_FOUND_COURSE.toException().getClass());

            verify(courseCommentRepository, never()).save(any());
        }

        @Test
        @DisplayName("현재 로그인한 회원 정보를 사용하여 댓글을 저장한다")
        void success_save_comment_with_login_member() {
            // given
            Course course = Course.builder()
                    .id(courseCommentSaveRequestDto.courseId())
                    .build();

            when(courseRepository.findById(courseCommentSaveRequestDto.courseId())).thenReturn(Optional.of(course));

            // when
            courseCommentSaveService.saveCourseComment(courseCommentSaveRequestDto);

            // then
            verify(courseCommentRepository).save(argThat(comment ->
                    comment.getMember().getId().equals(member.getId())
            ));
        }
    }
}