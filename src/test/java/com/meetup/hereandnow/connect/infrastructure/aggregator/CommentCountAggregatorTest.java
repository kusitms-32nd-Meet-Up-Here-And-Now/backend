package com.meetup.hereandnow.connect.infrastructure.aggregator;

import com.meetup.hereandnow.connect.infrastructure.repository.CoupleCourseCommentRepository;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseCommentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentCountAggregatorTest {

    @Mock
    private CourseCommentRepository courseCommentRepository;

    @Mock
    private CoupleCourseCommentRepository coupleCourseCommentRepository;

    @InjectMocks
    private CommentCountAggregator commentCountAggregator;

    @Test
    @DisplayName("두 레포지토리의 댓글 합산에 성공한다")
    void success_comment_count() {
        // given
        Course course = mock(Course.class);
        when(courseCommentRepository.countByCourse(course)).thenReturn(5);
        when(coupleCourseCommentRepository.countByCourse(course)).thenReturn(3);

        // when
        int result = commentCountAggregator.aggregate(course);

        // then
        assertThat(result).isEqualTo(8);
        verify(courseCommentRepository).countByCourse(course);
        verify(coupleCourseCommentRepository).countByCourse(course);
    }

    @Test
    @DisplayName("한쪽 레포지토리에 댓글이 있는 경우에도 댓글 합산에 성공한다.")
    void success_one_repository_count() {
        // given
        Course course = mock(Course.class);
        when(courseCommentRepository.countByCourse(course)).thenReturn(10);
        when(coupleCourseCommentRepository.countByCourse(course)).thenReturn(0);

        // when
        int result = commentCountAggregator.aggregate(course);

        // then
        assertThat(result).isEqualTo(10);
    }

    @Test
    @DisplayName("댓글이 없는 경우 0을 반환한다.")
    void success_not_comment_return_0() {
        // given
        Course course = mock(Course.class);
        when(courseCommentRepository.countByCourse(course)).thenReturn(0);
        when(coupleCourseCommentRepository.countByCourse(course)).thenReturn(0);

        // when
        int result = commentCountAggregator.aggregate(course);

        // then
        assertThat(result).isEqualTo(0);
    }
}

