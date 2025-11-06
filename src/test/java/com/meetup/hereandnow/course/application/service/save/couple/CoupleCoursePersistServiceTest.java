package com.meetup.hereandnow.course.application.service.save.couple;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import com.meetup.hereandnow.course.dto.request.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.course.dto.request.CoupleCourseRecordSaveRequestDto;
import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.connect.repository.CoupleRepository;
import com.meetup.hereandnow.pin.application.service.save.CouplePinImageSaveService;
import com.meetup.hereandnow.pin.application.service.save.CouplePinRecordSaveService;
import com.meetup.hereandnow.connect.domain.CouplePinRecord;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoupleCoursePersistServiceTest {

    @Mock
    private CoupleCourseImageSaveService coupleCourseImageSaveService;

    @Mock
    private CoupleCourseRecordSaveService coupleCourseRecordSaveService;

    @Mock
    private CouplePinRecordSaveService couplePinRecordSaveService;

    @Mock
    private CoupleRepository coupleRepository;

    @Mock
    private CouplePinImageSaveService couplePinImageSaveService;

    @InjectMocks
    private CoupleCoursePersistService coupleCoursePersistService;

    private static final String TEST_COURSE_TITLE = "코스 제목";
    private static final String TEST_COURSE_DESC = "코스 설명";
    private static final String TEST_COURSE_POSITIVE = "코스 좋은 점";
    private static final String TEST_COURSE_NEGATIVE = "코스 아쉬운 점";
    private static final LocalDate TEST_COURSE_VISIT_DATE = LocalDate.now();
    private static final String TEST_COURSE_WITH = "친구";
    private static final String TEST_COURSE_REGION = "마포";

    @Test
    @DisplayName("커플 기록이 정상적으로 저장된다.")
    void success_couple_course_save() {
        // given
        Member member = mock(Member.class);
        CourseSaveDto courseSaveDto = new CourseSaveDto(
                TEST_COURSE_TITLE,
                TEST_COURSE_DESC,
                TEST_COURSE_POSITIVE,
                TEST_COURSE_NEGATIVE,
                true,
                TEST_COURSE_VISIT_DATE,
                TEST_COURSE_WITH,TEST_COURSE_REGION,
                new CoupleCourseRecordSaveRequestDto("gDesc", "mDesc"),
                List.of()
        );
        Course course = mock(Course.class);
        CommitSaveCourseRequestDto commitDto = mock(CommitSaveCourseRequestDto.class);

        Couple couple = mock(Couple.class);
        when(coupleRepository.findByMember(member)).thenReturn(
                java.util.Optional.of(couple));

        CouplePinRecord couplePinRecord = mock(CouplePinRecord.class);
        when(coupleCourseRecordSaveService.saveCoupleCourseRecords(any(CoupleCourseRecordSaveRequestDto.class),
                eq(course), eq(couple)))
                .thenReturn(null);

        when(course.getPinList()).thenReturn(List.of());
        when(couplePinRecordSaveService.saveCouplePinRecords(anyList(), anyList(), eq(couple)))
                .thenReturn(List.of(couplePinRecord));

        when(commitDto.pinImageObjectKeyList()).thenReturn(List.of());

        // when
        coupleCoursePersistService.coupleCourseSavePersist(courseSaveDto, member, course, commitDto);

        // then
        verify(coupleRepository).findByMember(member);
        verify(coupleCourseRecordSaveService).saveCoupleCourseRecords(any(CoupleCourseRecordSaveRequestDto.class),
                eq(course), eq(couple));
        verify(coupleCourseImageSaveService).saveCoupleCourseImage(any(), eq(commitDto));
        verify(couplePinRecordSaveService).saveCouplePinRecords(anyList(), anyList(), eq(couple));
        verify(couplePinImageSaveService).saveCouplePinImages(anyList(), anyList());
    }

    @Test
    @DisplayName("dto가 null이면 아무 동작도 하지 않는다")
    void success_dto_is_null() {
        // given
        Member member = mock(Member.class);
        CourseSaveDto courseSaveDto = new CourseSaveDto(
                TEST_COURSE_TITLE,
                TEST_COURSE_DESC,
                TEST_COURSE_POSITIVE,
                TEST_COURSE_NEGATIVE,
                true,
                TEST_COURSE_VISIT_DATE,
                TEST_COURSE_WITH,TEST_COURSE_REGION,
                null,
                List.of()
        );
        Course course = mock(Course.class);
        CommitSaveCourseRequestDto commitDto = mock(CommitSaveCourseRequestDto.class);

        // when
        coupleCoursePersistService.coupleCourseSavePersist(courseSaveDto, member, course, commitDto);

        // then
        verifyNoInteractions(coupleRepository);
        verifyNoInteractions(coupleCourseRecordSaveService);
        verifyNoInteractions(coupleCourseImageSaveService);
        verifyNoInteractions(couplePinRecordSaveService);
        verifyNoInteractions(couplePinImageSaveService);
    }
}