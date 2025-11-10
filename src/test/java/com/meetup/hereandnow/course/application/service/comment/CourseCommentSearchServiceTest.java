package com.meetup.hereandnow.course.application.service.comment;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.domain.entity.CourseComment;
import com.meetup.hereandnow.course.dto.response.CourseCommentDto;
import com.meetup.hereandnow.course.dto.response.CourseCommentResponseDto;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.course.infrastructure.repository.CourseCommentRepository;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseCommentSearchServiceTest {

    @Mock
    private CourseCommentRepository courseCommentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseCommentSearchService courseCommentSearchService;

    @Nested
    @DisplayName("코스 댓글 조회 테스트")
    class GetCommentListTest {

        @Test
        @DisplayName("코스에 대한 댓글 리스트를 정상적으로 조회한다")
        void getCommentList_Success() {
            // given
            Long courseId = 1L;
            Course course = Course.builder()
                    .id(courseId)
                    .build();

            Member member1 = Member.builder()
                    .id(1L)
                    .nickname("김히어")
                    .profileImage("http://image1.jpg")
                    .build();

            Member member2 = Member.builder()
                    .id(2L)
                    .nickname("박데어")
                    .profileImage("http://image2.jpg")
                    .build();

            CourseComment comment1 = CourseComment.builder()
                    .id(1L)
                    .content("여기 장소 좋아요.")
                    .member(member1)
                    .course(course)
                    .build();

            CourseComment comment2 = CourseComment.builder()
                    .id(2L)
                    .content("다음에 또 가고싶어요!")
                    .member(member2)
                    .course(course)
                    .build();

            List<CourseComment> comments = Arrays.asList(comment1, comment2);

            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
            when(courseCommentRepository.findByCourse(course)).thenReturn(comments);

            // when
            CourseCommentResponseDto result = courseCommentSearchService.getCommentList(courseId);

            // then
            assertThat(result.count()).isEqualTo(2);
            assertThat(result.comments()).hasSize(2);
            assertThat(result.comments().getFirst().id()).isEqualTo(1L);
            assertThat(result.comments().getFirst().nickName()).isEqualTo("김히어");
            assertThat(result.comments().getFirst().content()).isEqualTo("여기 장소 좋아요.");
            assertThat(result.comments().get(1).id()).isEqualTo(2L);
            assertThat(result.comments().get(1).nickName()).isEqualTo("박데어");
            assertThat(result.comments().get(1).content()).isEqualTo("다음에 또 가고싶어요!");

            verify(courseRepository).findById(courseId);
            verify(courseCommentRepository).findByCourse(course);
        }

        @Test
        @DisplayName("존재하지 않는 코스 ID로 조회 시 예외를 발생시킨다")
        void fail_course_not_found() {
            // given
            Long nonExistentCourseId = 999L;

            when(courseRepository.findById(nonExistentCourseId)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> courseCommentSearchService.getCommentList(nonExistentCourseId))
                    .isInstanceOf(CourseErrorCode.NOT_FOUND_COURSE.toException().getClass());

            verify(courseRepository).findById(nonExistentCourseId);
            verify(courseCommentRepository, never()).findByCourse(any());
        }

        @Test
        @DisplayName("댓글이 없는 코스의 경우 빈 리스트를 반환한다")
        void success_empty_comment_return_empty_list() {
            // given
            Long courseId = 1L;
            Course course = Course.builder()
                    .id(courseId)
                    .build();

            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
            when(courseCommentRepository.findByCourse(course)).thenReturn(Collections.emptyList());

            // when
            CourseCommentResponseDto result = courseCommentSearchService.getCommentList(courseId);

            // then
            assertThat(result.count()).isZero();
            assertThat(result.comments()).isEmpty();

            verify(courseRepository).findById(courseId);
            verify(courseCommentRepository).findByCourse(course);
        }

        @Test
        @DisplayName("여러 댓글이 있을 때 모든 댓글을 DTO로 변환하여 반환한다")
        void success_convert_dto_for_multiple_comments() {
            // given
            Long courseId = 1L;
            Course course = Course.builder()
                    .id(courseId)
                    .build();

            Member member = Member.builder()
                    .id(1L)
                    .nickname("테스터")
                    .profileImage("http://profile.jpg")
                    .build();

            CourseComment comment1 = CourseComment.builder()
                    .id(1L)
                    .content("첫 번째 댓글")
                    .member(member)
                    .course(course)
                    .build();

            CourseComment comment2 = CourseComment.builder()
                    .id(2L)
                    .content("두 번째 댓글")
                    .member(member)
                    .course(course)
                    .build();

            CourseComment comment3 = CourseComment.builder()
                    .id(3L)
                    .content("세 번째 댓글")
                    .member(member)
                    .course(course)
                    .build();

            List<CourseComment> comments = Arrays.asList(comment1, comment2, comment3);

            when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
            when(courseCommentRepository.findByCourse(course)).thenReturn(comments);

            // when
            CourseCommentResponseDto result = courseCommentSearchService.getCommentList(courseId);

            // then
            assertThat(result.count()).isEqualTo(3);
            assertThat(result.comments()).hasSize(3);
            assertThat(result.comments())
                    .extracting(CourseCommentDto::id)
                    .containsExactly(1L, 2L, 3L);
            assertThat(result.comments())
                    .extracting(CourseCommentDto::content)
                    .containsExactly("첫 번째 댓글", "두 번째 댓글", "세 번째 댓글");

            verify(courseRepository).findById(courseId);
            verify(courseCommentRepository).findByCourse(course);
        }
    }
}
