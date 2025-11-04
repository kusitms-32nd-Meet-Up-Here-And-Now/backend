package com.meetup.hereandnow.course.application.save;

import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.core.util.UUIDUtils;
import com.meetup.hereandnow.course.application.service.save.course.CourseRedisService;
import com.meetup.hereandnow.course.application.service.save.course.CourseSaveService;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import com.meetup.hereandnow.course.dto.request.CoupleCourseRecordSaveRequestDto;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.dto.PinDirnameDto;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.place.dto.PlaceSaveDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

@ExtendWith(MockitoExtension.class)
class CourseSaveServiceTest {

    @Mock
    private CourseRedisService courseRedisService;

    @InjectMocks
    private CourseSaveService courseSaveService;

    private MockedStatic<SecurityUtils> securityUtilsMock;
    private MockedStatic<UUIDUtils> uuidUtilsMock;

    private Member dummymember;

    private static final Long TEST_MEMBER_ID = 1L;

    private static final String FIXED_UUID = "fixed-uuid";
    private static final String WRONG_UUID = "wrong-uuid";

    private static final String TEST_PLACE_NAME = "장소 이름";
    private static final String TEST_PLACE_STREET_ADDRESS = "장소 도로명 주소";
    private static final String TEST_PLACE_NUMBER_ADDRESS = "장소 지번 주소";
    private static final double TEST_LAT = 37.1;
    private static final double TEST_LON = 127.1;

    private static final String TEST_PIN_POSITIVE = "핀 좋은 점";
    private static final String TEST_PIN_NEGATIVE = "핀 나쁜 점";
    private static final String TEST_PLACE_CODE = "CT1";
    private static final double TEST_PIN_RATING = 4.5;

    private static final String TEST_COURSE_TITLE = "코스 제목";
    private static final String TEST_COURSE_DESC = "코스 설명";
    private static final double TEST_COURSE_RATING = 4.5;

    @BeforeEach
    void setUp() {
        securityUtilsMock = org.mockito.Mockito.mockStatic(SecurityUtils.class);
        uuidUtilsMock = org.mockito.Mockito.mockStatic(UUIDUtils.class);

        dummymember = Member.builder().id(TEST_MEMBER_ID).build();
    }

    @AfterEach
    void tearDown() {
        securityUtilsMock.close();
        uuidUtilsMock.close();
    }

    @Test
    @DisplayName("코스 메타데이터가 정상적으로 레디스에 저장된다.")
    void success_save_course_to_redis() {

        // given
        securityUtilsMock.when(SecurityUtils::getCurrentMember).thenReturn(dummymember);
        uuidUtilsMock.when(UUIDUtils::getUUID).thenReturn(FIXED_UUID);

        PlaceSaveDto placeDto = new PlaceSaveDto(
                TEST_PLACE_NAME,
                TEST_PLACE_STREET_ADDRESS,
                TEST_PLACE_NUMBER_ADDRESS,
                TEST_LAT,
                TEST_LON
        );

        PinSaveDto pinDto = new PinSaveDto(
                TEST_PIN_RATING,
                TEST_PIN_POSITIVE,
                TEST_PIN_NEGATIVE,
                TEST_PLACE_CODE,
                List.of(),
                null,
                placeDto
        );

        CourseSaveDto courseSaveDto = new CourseSaveDto(
                TEST_COURSE_TITLE, TEST_COURSE_RATING, TEST_COURSE_DESC,
                Boolean.TRUE, null, null, null, List.of(pinDto)
        );

        // when
        var response = courseSaveService.saveCourseToRedis(courseSaveDto);

        // then
        assertThat(response.courseKey()).isEqualTo(FIXED_UUID);
        assertThat(response.courseDirname()).isEqualTo("course/" + FIXED_UUID + "/image");
        assertThat(response.pinDirname()).hasSize(1);
        PinDirnameDto pinDir = response.pinDirname().getFirst();
        assertThat(pinDir.pinIdx()).isZero();

        verify(courseRedisService).saveCourse(dummymember, FIXED_UUID, courseSaveDto);
    }

    @Test
    @DisplayName("커플 정보가 포함된 코스가 정상적으로 레디스에 저장된다.")
    void success_save_course_to_redis_with_couple() {

        // given
        securityUtilsMock.when(SecurityUtils::getCurrentMember).thenReturn(dummymember);
        uuidUtilsMock.when(UUIDUtils::getUUID).thenReturn(FIXED_UUID);

        PlaceSaveDto placeDto = new PlaceSaveDto(
                TEST_PLACE_NAME,
                TEST_PLACE_STREET_ADDRESS,
                TEST_PLACE_NUMBER_ADDRESS,
                TEST_LAT,
                TEST_LON
        );

        PinSaveDto pinDto = new PinSaveDto(
                TEST_PIN_RATING,
                TEST_PIN_POSITIVE,
                TEST_PIN_NEGATIVE,
                TEST_PLACE_CODE,
                List.of(),
                null,
                placeDto
        );

        CoupleCourseRecordSaveRequestDto coupleDto = new CoupleCourseRecordSaveRequestDto(
                "여자친구 설명", "남자친구 설명"
        );
        CourseSaveDto courseSaveDto = new CourseSaveDto(
                TEST_COURSE_TITLE, TEST_COURSE_RATING, TEST_COURSE_DESC,
                Boolean.TRUE, null, null, coupleDto, List.of(pinDto)
        );

        // when
        var response = courseSaveService.saveCourseToRedis(courseSaveDto);

        // then
        assertThat(response.courseKey()).isEqualTo(FIXED_UUID);
        assertThat(response.courseDirname()).isEqualTo("course/" + FIXED_UUID + "/image");
        assertThat(response.pinDirname()).hasSize(1);
        PinDirnameDto pinDir = response.pinDirname().getFirst();
        assertThat(pinDir.pinIdx()).isZero();

        verify(courseRedisService).saveCourse(dummymember, FIXED_UUID, courseSaveDto);
    }

    @Test
    @DisplayName("레디스에 저장된 메타데이터가 없는 경우 오류를 반환한다.")
    void fail_commit_save_not_found_in_redis() {

        securityUtilsMock.when(SecurityUtils::getCurrentMember).thenReturn(dummymember);
        given(courseRedisService.getCourse(dummymember, WRONG_UUID)).willReturn(null);

        assertThatThrownBy(() -> courseSaveService.commitSave(WRONG_UUID, null))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("저장된 코스 메타데이터를 찾을 수 없습니다.");
    }
}
