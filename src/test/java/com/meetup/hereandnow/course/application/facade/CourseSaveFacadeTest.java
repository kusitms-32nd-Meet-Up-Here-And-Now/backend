package com.meetup.hereandnow.course.application.facade;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.course.application.service.save.course.CourseSaveService;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import com.meetup.hereandnow.course.dto.request.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.course.dto.response.CourseSaveResponseDto;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.pin.dto.PinImageObjectKeyDto;
import com.meetup.hereandnow.pin.exception.PinErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CourseSaveFacadeTest {

    @Mock
    private ObjectStorageService objectStorageService;

    @Mock
    private CourseSaveService courseSaveService;

    @InjectMocks
    private CourseSaveFacade courseSaveFacade;

    private static final String TEST_PIN_IMAGE_OBJECT_KEY = "course/uuid/pins/1/images/1.jpg";
    private static final String TEST_COUPLE_PIN_IMAGE_KEY = "course/uuid/pins/1/images/couple.jpg";
    private static final String TEST_COUPLE_COURSE_IMAGE_KEY = "course/uuid/couple.jpg";

    private static final String TEST_COURSE_TITLE = "코스 제목";
    private static final String TEST_COURSE_DESC = "코스 설명";
    private static final double TEST_COURSE_RATING = 4.5;
    private static final String TEST_COURSE_UUID = "uuid";
    private static final String TEST_COURSE_DIRNAME = "/course/uuid/image";

    @Test
    @DisplayName("코스 메타데이터를 레디스에 저장하는 것에 성공한다.")
    void success_course_metadata_to_redis() {

        // given
        CourseSaveDto dto = new CourseSaveDto(TEST_COURSE_TITLE, TEST_COURSE_RATING, TEST_COURSE_DESC,
                true, null, null, null ,List.of());
        CourseSaveResponseDto responseDto = new CourseSaveResponseDto(TEST_COURSE_UUID, TEST_COURSE_DIRNAME, List.of());
        given(courseSaveService.saveCourseToRedis(dto)).willReturn(responseDto);

        // when
        var res = courseSaveFacade.prepareCourseSave(dto);

        // then
        assertThat(res).isSameAs(responseDto);
        verify(courseSaveService).saveCourseToRedis(dto);
    }

    @Test
    @DisplayName("이미지가 존재하는 경우 저장에 성공한다.")
    void success_save_course_exists_image() {

        // given
        PinImageObjectKeyDto pinDto = new PinImageObjectKeyDto(0, List.of(TEST_PIN_IMAGE_OBJECT_KEY), null);
        CommitSaveCourseRequestDto commitDto = new CommitSaveCourseRequestDto(
                null,
                List.of(pinDto)
        );

        given(objectStorageService.exists(TEST_PIN_IMAGE_OBJECT_KEY)).willReturn(true);
        given(courseSaveService.commitSave(TEST_COURSE_UUID, commitDto)).willReturn(123L);

        // when
        var response = courseSaveFacade.commitSaveCourse(TEST_COURSE_UUID, commitDto);

        // then
        assertThat(response.courseId()).isEqualTo(123L);
    }

    @Test
    @DisplayName("핀 이미지가 존재하지 않는 경우 오류가 발생한다.")
    void fail_pin_image_not_found() {

        // given
        PinImageObjectKeyDto pinDto = new PinImageObjectKeyDto(0, List.of(TEST_PIN_IMAGE_OBJECT_KEY), null);
        CommitSaveCourseRequestDto commitDto = new CommitSaveCourseRequestDto(
                null,
                List.of(pinDto)
        );

        given(objectStorageService.exists(TEST_PIN_IMAGE_OBJECT_KEY)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> courseSaveFacade.commitSaveCourse(TEST_COURSE_UUID, commitDto))
                .isInstanceOf(PinErrorCode.NOT_FOUND_PIN_IMAGE.toException().getClass())
                .hasMessageContaining("핀 이미지를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("커플 코스 이미지가 존재하지 않는 경우 오류가 발생한다.")
    void fail_couple_course_image_not_found() {

        // when
        CommitSaveCourseRequestDto commitDto = new CommitSaveCourseRequestDto(
                List.of(TEST_COUPLE_COURSE_IMAGE_KEY),
                List.of()
        );

        given(objectStorageService.exists(TEST_COUPLE_COURSE_IMAGE_KEY)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> courseSaveFacade.commitSaveCourse(TEST_COURSE_UUID, commitDto))
                .isInstanceOf(CourseErrorCode.NOT_FOUND_COUPLE_COURSE_IMAGE.toException().getClass())
                .hasMessageContaining("저장된 커플 기록 코스 이미지를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("커플 핀 이미지 기록이 존재하지 않는 경우 오류가 발생한다.")
    void fail_couple_pin_image_not_found() {

        // when
        PinImageObjectKeyDto pinDto = new PinImageObjectKeyDto(
                0, List.of(TEST_PIN_IMAGE_OBJECT_KEY), List.of(TEST_COUPLE_PIN_IMAGE_KEY)
        );
        CommitSaveCourseRequestDto commitDto = new CommitSaveCourseRequestDto(
                List.of(TEST_COUPLE_COURSE_IMAGE_KEY),
                List.of(pinDto)
        );

        given(objectStorageService.exists(TEST_COUPLE_COURSE_IMAGE_KEY)).willReturn(true);
        given(objectStorageService.exists(TEST_PIN_IMAGE_OBJECT_KEY)).willReturn(true);
        given(objectStorageService.exists(TEST_COUPLE_PIN_IMAGE_KEY)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> courseSaveFacade.commitSaveCourse(TEST_COURSE_UUID, commitDto))
                .isInstanceOf(PinErrorCode.NOT_FOUND_COUPLE_PIN_IMAGE.toException().getClass())
                .hasMessageContaining("저장 된 커플 기록 핀 이미지를 찾을 수 없습니다.");
    }
}

