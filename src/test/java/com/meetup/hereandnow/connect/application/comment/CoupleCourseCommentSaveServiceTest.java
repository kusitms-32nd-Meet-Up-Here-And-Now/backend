package com.meetup.hereandnow.connect.application.comment;

import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.connect.domain.CoupleCourseImageComment;
import com.meetup.hereandnow.connect.domain.CoupleCourseTextComment;
import com.meetup.hereandnow.connect.dto.request.CoupleCourseImageCommentRequestDto;
import com.meetup.hereandnow.connect.dto.request.CoupleCourseTextCommentRequestDto;
import com.meetup.hereandnow.connect.exception.CoupleCourseCommentErrorCode;
import com.meetup.hereandnow.connect.exception.CoupleErrorCode;
import com.meetup.hereandnow.connect.infrastructure.repository.CoupleCourseCommentRepository;
import com.meetup.hereandnow.connect.infrastructure.repository.CoupleRepository;
import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
    private CoupleRepository coupleRepository;

    @Mock
    private Member member;

    @Mock
    private Member anotherMember;

    @Mock
    private Course course;

    @Mock
    private Couple couple;

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
    @DisplayName("텍스트 댓글 저장에 성공한다.")
    void success_add_text_comment() {
        // given
        var dto = new CoupleCourseTextCommentRequestDto(1L, "좋은 코스네요!");

        when(courseRepository.findById(dto.courseId())).thenReturn(Optional.of(course));
        when(coupleRepository.findByMember(member)).thenReturn(Optional.of(couple));
        when(course.getMember()).thenReturn(member);
        when(couple.getMember1()).thenReturn(member);

        // when
        commentSaveService.addTextComment(dto);

        // then
        verify(coupleCourseCommentRepository).save(any(CoupleCourseTextComment.class));
    }

    @Test
    @DisplayName("이미지 댓글 저장에 성공한다.")
    void success_add_image_comment() {
        // given
        var dto = new CoupleCourseImageCommentRequestDto(1L, "course/1/couple/comment/uuid.png");

        when(courseRepository.findById(dto.courseId())).thenReturn(Optional.of(course));
        when(coupleRepository.findByMember(member)).thenReturn(Optional.of(couple));
        when(course.getMember()).thenReturn(member);
        when(couple.getMember1()).thenReturn(member);
        when(objectStorageService.exists(dto.objectKey())).thenReturn(true);

        // when
        commentSaveService.addImageComment(dto);

        // then
        verify(coupleCourseCommentRepository).save(any(CoupleCourseImageComment.class));
    }

    @Test
    @DisplayName("presigned url 반환에 성공한다.")
    void success_get_presigned_dirname() {
        // given
        Long courseId = 10L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(coupleRepository.findByMember(member)).thenReturn(Optional.of(couple));
        when(course.getMember()).thenReturn(member);
        when(couple.getMember1()).thenReturn(member);

        // when
        var result = commentSaveService.getPresignedDirname(courseId);

        // then
        assertThat(result.dirname()).isEqualTo("/course/10/couple/comment");
    }

    @Test
    @DisplayName("object storage에 이미지가 없는 경우 댓글 저장에 실패한다.")
    void fail_add_image_comment_not_saved_image() {
        // given
        var dto = new CoupleCourseImageCommentRequestDto(1L, "course/1/couple/comment/uuid.png");

        when(courseRepository.findById(dto.courseId())).thenReturn(Optional.of(course));
        when(coupleRepository.findByMember(member)).thenReturn(Optional.of(couple));
        when(course.getMember()).thenReturn(member);
        when(couple.getMember1()).thenReturn(member);
        when(objectStorageService.exists(dto.objectKey())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> commentSaveService.addImageComment(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(CoupleCourseCommentErrorCode.NOT_SAVED_IMAGE.getMessage());
    }

    @Test
    @DisplayName("커플이 아닌 경우 텍스트 댓글 저장에 실패한다.")
    void fail_add_text_comment_not_couple() {
        // given
        var dto = new CoupleCourseTextCommentRequestDto(1L, "좋은 코스네요!");

        when(courseRepository.findById(dto.courseId())).thenReturn(Optional.of(course));
        when(coupleRepository.findByMember(member)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentSaveService.addTextComment(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(CoupleErrorCode.NOT_FOUND_COUPLE.getMessage());
    }

    @Test
    @DisplayName("커플이 아닌 경우 이미지 댓글 저장에 실패한다.")
    void fail_add_image_comment_not_couple() {
        // given
        var dto = new CoupleCourseImageCommentRequestDto(1L, "course/1/couple/comment/uuid.png");

        when(courseRepository.findById(dto.courseId())).thenReturn(Optional.of(course));
        when(coupleRepository.findByMember(member)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentSaveService.addImageComment(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(CoupleErrorCode.NOT_FOUND_COUPLE.getMessage());
    }

    @Test
    @DisplayName("커플이 아닌 경우 presigned url 반환에 실패한다")
    void fail_get_presigned_dirname_not_couple() {
        // given
        Long courseId = 10L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(coupleRepository.findByMember(member)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentSaveService.getPresignedDirname(courseId))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(CoupleErrorCode.NOT_FOUND_COUPLE.getMessage());
    }

    @Test
    @DisplayName("다른 커플의 코스인 경우 텍스트 댓글 저장에 실패한다.")
    void fail_add_text_comment_not_equal_course_couple() {
        // given
        var dto = new CoupleCourseTextCommentRequestDto(1L, "좋은 코스네요!");

        when(courseRepository.findById(dto.courseId())).thenReturn(Optional.of(course));
        when(coupleRepository.findByMember(member)).thenReturn(Optional.of(couple));
        when(course.getMember()).thenReturn(anotherMember); // 다른 사람의 코스
        when(couple.getMember1()).thenReturn(member);
        when(couple.getMember2()).thenReturn(mock(Member.class)); // 현재 사용자와 다른 커플 멤버

        // when & then
        assertThatThrownBy(() -> commentSaveService.addTextComment(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(CoupleCourseCommentErrorCode.NOT_EQUAL_COURSE_COUPLE.getMessage());
    }

    @Test
    @DisplayName("다른 커플의 코스인 경우 이미지 댓글 저장에 실패한다.")
    void fail_add_image_comment_not_equal_course_couple() {
        // given
        var dto = new CoupleCourseImageCommentRequestDto(1L, "course/1/couple/comment/uuid.png");

        when(courseRepository.findById(dto.courseId())).thenReturn(Optional.of(course));
        when(coupleRepository.findByMember(member)).thenReturn(Optional.of(couple));
        when(course.getMember()).thenReturn(anotherMember); // 다른 사람의 코스
        when(couple.getMember1()).thenReturn(member);
        when(couple.getMember2()).thenReturn(mock(Member.class)); // 현재 사용자와 다른 커플 멤버

        // when & then
        assertThatThrownBy(() -> commentSaveService.addImageComment(dto))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(CoupleCourseCommentErrorCode.NOT_EQUAL_COURSE_COUPLE.getMessage());
    }

    @Test
    @DisplayName("다른 커플의 코스인 경우 presigned url 저장에 실패한다.")
    void fail_get_presigned_dirname_not_equal_course_couple() {
        // given
        Long courseId = 10L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(coupleRepository.findByMember(member)).thenReturn(Optional.of(couple));
        when(course.getMember()).thenReturn(anotherMember); // 다른 사람의 코스
        when(couple.getMember1()).thenReturn(member);
        when(couple.getMember2()).thenReturn(mock(Member.class)); // 현재 사용자와 다른 커플 멤버

        // when & then
        assertThatThrownBy(() -> commentSaveService.getPresignedDirname(courseId))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining(CoupleCourseCommentErrorCode.NOT_EQUAL_COURSE_COUPLE.getMessage());
    }
}
