package com.meetup.hereandnow.connect.application.comment;

import com.meetup.hereandnow.connect.domain.CoupleCourseComment;
import com.meetup.hereandnow.connect.dto.response.CoupleCourseCommentResponseDto;
import com.meetup.hereandnow.connect.infrastructure.repository.CoupleCourseCommentRepository;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoupleCourseCommentReadServiceTest {

    @InjectMocks
    private CoupleCourseCommentReadService commentReadService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CoupleCourseCommentRepository coupleCourseCommentRepository;

    @Mock
    private Member member;

    @Mock
    private Course course;

    private MockedStatic<SecurityUtils> mockedSecurity;
    private MockedStatic<CoupleCourseCommentResponseDto> mockedDto;

    @BeforeEach
    void setUp() {
        mockedSecurity = mockStatic(SecurityUtils.class);
        when(SecurityUtils.getCurrentMember()).thenReturn(member);
    }

    @AfterEach
    void tearDown() {
        mockedSecurity.close();
        if(mockedDto != null) {
            mockedDto.close();
        }
    }

    @Test
    @DisplayName("댓글 목록 조회 성공 - createdAt 오름차순 정렬")
    void getComments_success() {
        // given
        Long courseId = 1L;

        CoupleCourseComment comment1 = mock(CoupleCourseComment.class);
        CoupleCourseComment comment2 = mock(CoupleCourseComment.class);

        CoupleCourseCommentResponseDto dto1 = new CoupleCourseCommentResponseDto(
                        1L, "text", "좋아요!",
                null, 1L, "테스트 유저1", LocalDateTime.now()
                );
        CoupleCourseCommentResponseDto dto2 = new CoupleCourseCommentResponseDto(2L, "image", null,
                "course/1/couple/comment/abc.png", 2L, "테스트 유저2", LocalDateTime.now());

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(coupleCourseCommentRepository.findAllByCourseOrderByCreatedAtAsc(course))
                .thenReturn(List.of(comment1, comment2));

        mockStatic(CoupleCourseCommentResponseDto.class);
        when(CoupleCourseCommentResponseDto.from(comment1)).thenReturn(dto1);
        when(CoupleCourseCommentResponseDto.from(comment2)).thenReturn(dto2);

        // when
        List<CoupleCourseCommentResponseDto> result = commentReadService.getComments(courseId);

        // then
        assertThat(result).hasSize(2);

        verify(courseRepository).findById(courseId);
        verify(coupleCourseCommentRepository).findAllByCourseOrderByCreatedAtAsc(course);
    }
}
