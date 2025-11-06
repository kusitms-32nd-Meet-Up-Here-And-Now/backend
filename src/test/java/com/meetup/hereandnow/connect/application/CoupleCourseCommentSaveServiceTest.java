package com.meetup.hereandnow.connect.application;

import com.meetup.hereandnow.connect.domain.CoupleCourseImageComment;
import com.meetup.hereandnow.connect.domain.CoupleCourseTextComment;
import com.meetup.hereandnow.connect.dto.request.CoupleCourseImageCommentRequestDto;
import com.meetup.hereandnow.connect.dto.request.CoupleCourseTextCommentRequestDto;
import com.meetup.hereandnow.connect.exception.CoupleCourseCommentErrorCode;
import com.meetup.hereandnow.connect.repository.CoupleCourseCommentRepository;
import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoupleCourseCommentSaveServiceTest {

    @InjectMocks
    private CoupleCourseCommentSaveService commentSaveService;

    @Mock
    private CoupleCourseCommentRepository coupleCourseCommentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private ObjectStorageService objectStorageService;

    @Mock
    private Member member;

    @Mock
    private Course course;

    private MockedStatic<SecurityUtils> mockedSecurity;

    @BeforeEach
    void setUp() {
        mockedSecurity = mockStatic(SecurityUtils.class);
        when(SecurityUtils.getCurrentMember()).thenReturn(member);
    }

    @AfterEach
    void tearDown() {
        mockedSecurity.close();
    }

    @Test
    @DisplayName("텍스트 댓글 저장 성공")
    void addTextComment_success() {
        // given
        var dto = new CoupleCourseTextCommentRequestDto(1L, "좋은 코스네요!");

        when(courseRepository.findById(dto.courseId())).thenReturn(Optional.of(course));

        // when
        commentSaveService.addTextComment(dto);

        // then
        verify(courseRepository).findById(dto.courseId());
        verify(coupleCourseCommentRepository).save(any(CoupleCourseTextComment.class));
    }

    @Test
    @DisplayName("이미지 댓글 저장 성공 (objectStorage 존재 확인 후)")
    void addImageComment_success() {
        // given
        var dto = new CoupleCourseImageCommentRequestDto(1L, "course/1/couple/comment/uuid.png");

        when(courseRepository.findById(dto.courseId())).thenReturn(Optional.of(course));
        when(objectStorageService.exists(dto.objectKey())).thenReturn(true);

        // when
        commentSaveService.addImageComment(dto);

        // then
        verify(objectStorageService).exists(dto.objectKey());
        verify(coupleCourseCommentRepository).save(any(CoupleCourseImageComment.class));
    }

    @Test
    @DisplayName("이미지 댓글 저장 실패 - ObjectStorage에 없는 경우 예외 발생")
    void addImageComment_notSavedImage_throwsException() {
        // given
        var dto = new CoupleCourseImageCommentRequestDto(1L, "course/1/couple/comment/uuid.png");

        when(courseRepository.findById(dto.courseId())).thenReturn(Optional.of(course));
        when(objectStorageService.exists(dto.objectKey())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> commentSaveService.addImageComment(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(CoupleCourseCommentErrorCode.NOT_SAVED_IMAGE.getMessage());

        verify(coupleCourseCommentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Presigned dirname 반환 성공")
    void getPresignedDirname_success() {
        // given
        Long courseId = 10L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // when
        var result = commentSaveService.getPresignedDirname(courseId);

        // then
        assertThat(result.dirname()).isEqualTo("/course/10/couple/comment");
        verify(courseRepository).findById(courseId);
    }
}
