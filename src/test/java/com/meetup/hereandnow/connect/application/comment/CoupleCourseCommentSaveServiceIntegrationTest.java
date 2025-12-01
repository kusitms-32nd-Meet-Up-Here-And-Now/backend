package com.meetup.hereandnow.connect.application.comment;

import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.connect.domain.CoupleCourseComment;
import com.meetup.hereandnow.connect.domain.value.CoupleStatus;
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
import com.meetup.hereandnow.integration.fixture.course.CourseEntityFixture;
import com.meetup.hereandnow.integration.fixture.member.MemberEntityFixture;
import com.meetup.hereandnow.integration.support.IntegrationTestSupport;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.infrastructure.repository.MemberRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class CoupleCourseCommentSaveServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private CoupleCourseCommentRepository coupleCourseCommentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private ObjectStorageService objectStorageService;

    @Autowired
    private CoupleRepository coupleRepository;

    @Autowired
    private CoupleCourseCommentSaveService coupleCourseCommentSaveService;

    private Couple couple;
    private Course course;
    private Member member1;
    private Member member2;
    private MockedStatic<SecurityUtils> mockedSecurity;

    private CoupleCourseTextCommentRequestDto requestDto;

    @BeforeEach
    void setUp() {
        member1 = MemberEntityFixture.getMember(1);
        member2 = MemberEntityFixture.getMember(2);

        memberRepository.saveAll(List.of(member1, member2));

        mockedSecurity = mockStatic(SecurityUtils.class);

        couple = Couple.builder()
                .member1(member1)
                .member2(member2)
                .coupleStartDate(LocalDate.now())
                .coupleBannerImageUrl("https://kr.objectstorage.com")
                .coupleStatus(CoupleStatus.ACCEPTED)
                .build();

        coupleRepository.save(couple);

        course = CourseEntityFixture.getCourse(member2);
        courseRepository.save(course);

        requestDto = new CoupleCourseTextCommentRequestDto(
                course.getId(),
                "테스트 댓글"
        );
    }

    @AfterEach
    void tearDown() {
        mockedSecurity.close();
    }

    @Nested
    @DisplayName("커플 코스 댓글 테스트 - 글 작성 통합 테스트")
    class CoupleCourseTextCommentTest {

        @Test
        @DisplayName("커플 상태일 때 다른 사람이 올린 코스에도 글을 작성 할 수 있다.")
        void success_save_comment_by_other_person_course() {
            // given
            mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(member2);

            // when
            coupleCourseCommentSaveService.addTextComment(requestDto);

            // then
            List<CoupleCourseComment> commentList = coupleCourseCommentRepository.findAll();
            System.out.println(commentList.toString());
            assertAll(
                    () -> assertThat(commentList.size()).isOne(),
                    () -> assertThat(commentList.getFirst().getCourse()).isEqualTo(course),
                    () -> assertThat(commentList.getFirst().getMember()).isEqualTo(member2)
            );
        }

        @Test
        @DisplayName("커플이 아닌 다른 사람은 댓글을 쓸 수 없다")
        void fail_is_not_my_couple_course() {
            // given
            Member wrongMember = MemberEntityFixture.getMember(3);
            memberRepository.save(wrongMember);
            mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(wrongMember);

            // when & then
            assertThatThrownBy(() -> coupleCourseCommentSaveService.addTextComment(requestDto))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(CoupleErrorCode.NOT_FOUND_COUPLE.getMessage());
        }

        @Test
        @DisplayName("커플이어도 커플의 코스만 댓글을 쓸 수 있다.")
        void fail_is_not_couple_course() {
            // given
            Member wrongMember = MemberEntityFixture.getMember(3);
            memberRepository.save(wrongMember);
            mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(member1);

            Course wrongCourse = CourseEntityFixture.getCourse(wrongMember);
            courseRepository.save(wrongCourse);

            requestDto = new CoupleCourseTextCommentRequestDto(
                    wrongCourse.getId(),
                    "테스트 댓글"
            );

            // when & then
            assertThatThrownBy(() -> coupleCourseCommentSaveService.addTextComment(requestDto))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(CoupleCourseCommentErrorCode.NOT_EQUAL_COURSE_COUPLE.getMessage());
        }
    }
}
