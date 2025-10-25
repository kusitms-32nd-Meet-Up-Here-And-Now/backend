package com.meetup.hereandnow.course.application.save;

import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.core.util.UUIDUtils;
import com.meetup.hereandnow.course.application.service.save.CourseSaveService;
import com.meetup.hereandnow.course.application.service.save.CourseRedisService;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import com.meetup.hereandnow.pin.dto.PinDirnameDto;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.place.dto.PlaceSaveDto;
import com.meetup.hereandnow.member.domain.Member;
import java.util.List;
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
    private static final String TEST_PLACE_ADDRESS = "장소 주소";
    private static final double TEST_LAT = 37.1;
    private static final double TEST_LON = 127.1;

    private static final String TEST_PIN_TITLE = "핀 제목";
    private static final String TEST_PIN_DESC = "핀 설명";
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

        PlaceSaveDto placeDto = new PlaceSaveDto(TEST_PLACE_NAME, TEST_PLACE_ADDRESS, TEST_LAT, TEST_LON);
        PinSaveDto pinDto = new PinSaveDto(TEST_PIN_TITLE, TEST_PIN_RATING, TEST_PIN_DESC, List.of(), placeDto);

        CourseSaveDto courseSaveDto = new CourseSaveDto(
                TEST_COURSE_TITLE, TEST_COURSE_RATING, TEST_COURSE_DESC, Boolean.TRUE, List.of(), List.of(pinDto)
        );

        // when
        var response = courseSaveService.saveCourseToRedis(courseSaveDto);

        assertThat(response.courseKey()).isEqualTo(FIXED_UUID);
        assertThat(response.courseDirname()).isEqualTo("/course/" + FIXED_UUID + "/image");
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
