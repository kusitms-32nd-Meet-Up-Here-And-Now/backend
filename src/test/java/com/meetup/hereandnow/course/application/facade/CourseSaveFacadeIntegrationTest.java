package com.meetup.hereandnow.course.application.facade;

import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.application.service.save.course.CourseRedisService;
import com.meetup.hereandnow.course.application.service.save.course.CourseSaveService;
import com.meetup.hereandnow.course.dto.request.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.course.dto.request.CourseSaveDto;
import com.meetup.hereandnow.course.dto.response.CommitSaveCourseResponseDto;
import com.meetup.hereandnow.course.fixture.CourseFixture;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.integration.support.IntegrationTestSupport;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.domain.value.Provider;
import com.meetup.hereandnow.member.infrastructure.repository.MemberRepository;
import com.meetup.hereandnow.pin.dto.PinImageObjectKeyDto;
import com.meetup.hereandnow.pin.exception.PinErrorCode;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

class CourseSaveFacadeIntegrationTest extends IntegrationTestSupport {

    @MockBean
    private ObjectStorageService objectStorageService;

    @Autowired
    private CourseSaveService courseSaveService;

    @Autowired
    private CourseSaveFacade courseSaveFacade;

    @Autowired
    private CourseRedisService courseRedisService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CourseRepository courseRepository;

    private static final String TEST_PIN_IMAGE_OBJECT_KEY = "course/uuid/pins/1/images/1.jpg";
    private static final String TEST_COURSE_UUID = "uuid";

    private MockedStatic<SecurityUtils> mockedSecurity;
    private Member member;

    @BeforeEach
    void setUp() {
        member = Member.builder()
                .nickname("테스트 유저")
                .email("test01@test.com")
                .profileImage("test")
                .providerId("123141412412")
                .provider(Provider.GOOGLE)
                .username("test01")
                .build();

        memberRepository.save(member);

        mockedSecurity = Mockito.mockStatic(SecurityUtils.class);

        mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(member);
    }

    @AfterEach
    void tearDown() {
        mockedSecurity.close();
        courseRepository.deleteAll();
        memberRepository.deleteAll();
    }

    private CommitSaveCourseRequestDto requestWithKey(String key) {
        PinImageObjectKeyDto pin = new PinImageObjectKeyDto(0, List.of(key));
        return new CommitSaveCourseRequestDto(List.of(pin));
    }

    @Nested
    @DisplayName("코스 저장 통합 테스트")
    class CourseSave {

        @Test
        @DisplayName("prepare 후 commit 전체 플로우가 정상적으로 동작한다.")
        void success_prepare_then_commit_flow() {
            CourseSaveDto courseSaveDto = CourseFixture.course();

            when(objectStorageService.exists(TEST_PIN_IMAGE_OBJECT_KEY)).thenReturn(true);

            // prepare
            var prepareResponse = courseSaveFacade.prepareCourseSave(courseSaveDto);

            // then
            String actualCourseUuid = prepareResponse.courseKey();
            assertThat(actualCourseUuid).isNotNull();
            assertThat(courseRedisService.getCourse(member, actualCourseUuid)).isNotNull();

            // commit
            CommitSaveCourseRequestDto request = buildRequest(TEST_PIN_IMAGE_OBJECT_KEY);
            var commitResponse = courseSaveFacade.commitSaveCourse(actualCourseUuid, request);

            assertThat(commitResponse.courseId()).isNotNull();
        }

        @Test
        @DisplayName("핀 이미지가 존재하면 코스 저장에 성공한다.")
        void success_commit_save_with_existing_image() {
            // given
            var request = requestWithKey(TEST_PIN_IMAGE_OBJECT_KEY);
            courseRedisService.saveCourse(member, TEST_COURSE_UUID, CourseFixture.course());

            when(objectStorageService.exists(TEST_PIN_IMAGE_OBJECT_KEY)).thenReturn(true);

            // when
            System.out.println(SecurityUtils.getCurrentMember().getClass());
            var res = courseSaveFacade.commitSaveCourse(TEST_COURSE_UUID, request);

            // then
            assertThat(res.courseId()).isNotNull();
        }

        @Test
        @DisplayName("저장된 이미지가 없는 경우 코스 저장에 실패한다.")
        void fail_is_not_exists_image() {
            // given
            var request = requestWithKey("wrong-key");

            when(objectStorageService.exists(TEST_PIN_IMAGE_OBJECT_KEY)).thenReturn(false);

            // when & then
            assertThatThrownBy(() -> courseSaveFacade.commitSaveCourse(TEST_COURSE_UUID, request))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(PinErrorCode.NOT_FOUND_PIN_IMAGE.getMessage());
        }

        private CommitSaveCourseRequestDto buildRequest(String key) {
            PinImageObjectKeyDto pin = new PinImageObjectKeyDto(0, List.of(key));
            return new CommitSaveCourseRequestDto(List.of(pin));
        }
    }
}
