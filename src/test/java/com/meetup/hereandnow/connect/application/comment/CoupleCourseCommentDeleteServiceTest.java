package com.meetup.hereandnow.connect.application.comment;

import com.meetup.hereandnow.connect.domain.CoupleCourseImageComment;
import com.meetup.hereandnow.connect.domain.CoupleCourseTextComment;
import com.meetup.hereandnow.connect.exception.CoupleCourseCommentErrorCode;
import com.meetup.hereandnow.connect.infrastructure.repository.CoupleCourseCommentRepository;
import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
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
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CoupleCourseCommentDeleteServiceTest {

    @Mock
    private CoupleCourseCommentRepository coupleCourseCommentRepository;

    @Mock
    private ObjectStorageService objectStorageService;

    @InjectMocks
    private CoupleCourseCommentDeleteService coupleCourseCommentDeleteService;

    private MockedStatic<SecurityUtils> mockedSecurity;

    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder().id(1L).username("user").build();
        mockedSecurity = mockStatic(SecurityUtils.class);
        when(SecurityUtils.getCurrentMember()).thenReturn(member);
    }

    @AfterEach
    void tearDown() {
        mockedSecurity.close();
    }

    @Test
    @DisplayName("이미지 댓글 삭제에 성공한다.")
    void success_image_comment_delete() {
        // given
        CoupleCourseImageComment imageComment = CoupleCourseImageComment.of(mock(Course.class), member, "objectKey");

        given(coupleCourseCommentRepository.findById(1L)).willReturn(Optional.of(imageComment));
        given(objectStorageService.exists("objectKey")).willReturn(true);

        // when
        coupleCourseCommentDeleteService.deleteImageComment(1L);

        // then
        verify(objectStorageService).delete("objectKey");
        verify(coupleCourseCommentRepository).delete(imageComment);
    }

    @Test
    @DisplayName("댓글을 찾을 수 없으면 삭제에 실패한다.")
    void fail_comment_not_found() {
        // given
        given(coupleCourseCommentRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> coupleCourseCommentDeleteService.deleteImageComment(1L))
                .isInstanceOf(DomainException.class)
                .hasMessage(CoupleCourseCommentErrorCode.NOT_FOUND_COMMENT.getMessage());
    }

    @Test
    @DisplayName("본인의 댓글이 아니면 댓글 삭제에 실패한다.")
    void fail_is_not_mine() {
        // given
        Member wrongMember = Member.builder().id(2L).build();
        CoupleCourseImageComment imageComment = CoupleCourseImageComment.of(mock(Course.class), wrongMember,
                "objectKey");

        given(coupleCourseCommentRepository.findById(1L))
                .willReturn(Optional.of(imageComment));

        // when & then
        assertThatThrownBy(() -> coupleCourseCommentDeleteService.deleteImageComment(1L))
                .isInstanceOf(DomainException.class)
                .hasMessage(CoupleCourseCommentErrorCode.FORBIDDEN_COMMENT_DELETE.getMessage());
    }

    @Test
    @DisplayName("이미지 댓글이 아니면 삭제에 실패한다")
    void fail_not_image_comment() {
        // given
        CoupleCourseTextComment textComment = CoupleCourseTextComment.of(
                mock(Course.class),
                member,
                "나는 이미지가 아니다"
        );

        given(coupleCourseCommentRepository.findById(1L))
                .willReturn(Optional.of(textComment));

        assertThatThrownBy(() -> coupleCourseCommentDeleteService.deleteImageComment(1L))
                .isInstanceOf(DomainException.class)
                .hasMessage(CoupleCourseCommentErrorCode.NOT_IMAGE_COMMENT.getMessage());

    }
}
