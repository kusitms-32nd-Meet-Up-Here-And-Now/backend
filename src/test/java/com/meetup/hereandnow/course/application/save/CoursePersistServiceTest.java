package com.meetup.hereandnow.course.application.save;

import com.meetup.hereandnow.course.application.service.save.course.CoursePersistService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import com.meetup.hereandnow.course.dto.request.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.application.facade.PinSaveFacade;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.place.application.facade.PlaceSaveFacade;
import com.meetup.hereandnow.place.domain.Place;
import com.meetup.hereandnow.place.dto.PlaceSaveDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CoursePersistServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private PlaceSaveFacade placeSaveFacade;

    @Mock
    private PinSaveFacade pinSaveFacade;

    @InjectMocks
    private CoursePersistService coursePersistService;

    private Member member;

    private static final Long TEST_MEMBER_ID = 1L;

    private static final String TEST_PLACE_NAME = "장소 이름";
    private static final String TEST_PLACE_ADDRESS = "장소 주소";
    private static final double TEST_LAT = 37.1;
    private static final double TEST_LON = 127.1;

    private static final String TEST_PIN_TITLE = "핀 제목";
    private static final String TEST_PIN_DESC = "핀 설명";
    private static final String TEST_PLACE_CODE = "CT1";
    private static final double TEST_PIN_RATING = 4.5;

    private static final String TEST_COURSE_TITLE = "코스 제목";
    private static final String TEST_COURSE_DESC = "코스 설명";
    private static final double TEST_COURSE_RATING = 4.5;

    @BeforeEach
    void setUp() {
        member = Member.builder().id(TEST_MEMBER_ID).build();
    }

    @Test
    @DisplayName("성공적으로 코스 정보가 데이터베이스에 저장된다.")
    void success_persist_saves_course() {

        // given
        PlaceSaveDto placeDto = new PlaceSaveDto(TEST_PLACE_NAME, TEST_PLACE_ADDRESS, TEST_LAT, TEST_LON);
        PinSaveDto pinDto = new PinSaveDto(
                TEST_PIN_TITLE, TEST_PIN_RATING, TEST_PIN_DESC,
                TEST_PLACE_CODE, List.of(), null, placeDto
        );

        CourseSaveDto courseSaveDto = new CourseSaveDto(
                TEST_COURSE_TITLE, TEST_COURSE_RATING, TEST_COURSE_DESC,
                Boolean.TRUE, null, List.of(pinDto)
        );
        CommitSaveCourseRequestDto commitDto = new CommitSaveCourseRequestDto(
                "/course/uuid/image.jpg",
                null,
                List.of()
        );

        when(placeSaveFacade.findOrCreatePlaces(courseSaveDto.pinList())).thenReturn(
                Map.of("place|37.1|127.1", Place.builder().id(1L).build()));

        when(courseRepository.save(any(Course.class))).thenAnswer(invocation -> {
            Course c = invocation.getArgument(0);
            return Course.builder()
                    .id(100L)
                    .courseTitle(c.getCourseTitle())
                    .build();
        });

        // when
        coursePersistService.persist(courseSaveDto, member, commitDto);

        // then
        verify(courseRepository).save(any(Course.class));
        verify(placeSaveFacade).findOrCreatePlaces(courseSaveDto.pinList());
        verify(pinSaveFacade).savePinEntityToTable(anyList(), any(Course.class), anyMap(), eq(commitDto));
    }
}
