package com.meetup.hereandnow.course.application.service.save;

import com.meetup.hereandnow.course.application.service.save.couple.CoupleCourseRecordSaveService;
import com.meetup.hereandnow.course.domain.entity.CoupleCourseRecord;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.request.CoupleCourseRecordSaveRequestDto;
import com.meetup.hereandnow.course.infrastructure.repository.CoupleCourseRecordRepository;
import com.meetup.hereandnow.member.domain.Couple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CoupleCourseRecordSaveServiceTest {

    @Mock
    private CoupleCourseRecordRepository coupleCourseRecordRepository;

    @InjectMocks
    private CoupleCourseRecordSaveService coupleCourseRecordSaveService;

    private Course course;
    private Couple couple;

    @BeforeEach
    void setUp() {
        couple = Couple.builder().id(1L).build();
        course = Course.builder().id(1L).build();
    }

    @Test
    @DisplayName("커플 코스 기록 저장이 정상적으로 이뤄진다.")
    void success_couple_record_save() {

        // given
        CoupleCourseRecordSaveRequestDto dto =
                new CoupleCourseRecordSaveRequestDto("여자친구 설명", "남자친구 설명");

        CoupleCourseRecord savedRecord = CoupleCourseRecord.builder()
                .descriptionByGirlfriend(dto.descriptionByGirlfriend())
                .descriptionByBoyfriend(dto.descriptionByBoyfriend())
                .course(course)
                .couple(couple)
                .build();

        given(coupleCourseRecordRepository.save(any(CoupleCourseRecord.class))).willReturn(savedRecord);

        // when
        CoupleCourseRecord result = coupleCourseRecordSaveService.saveCoupleCourseRecords(dto, course, couple);

        // then
        assertThat(result.getDescriptionByBoyfriend()).isEqualTo("남자친구 설명");
        assertThat(result.getDescriptionByGirlfriend()).isEqualTo("여자친구 설명");
        assertThat(result.getCourse()).isEqualTo(course);
        assertThat(result.getCouple()).isEqualTo(couple);
    }

    @Test
    @DisplayName("dto가 null인 경우 저장이 이뤄지지 않는다")
    void success_not_save_dto_is_null() {
        // when
        CoupleCourseRecord result = coupleCourseRecordSaveService.saveCoupleCourseRecords(null, course, couple);

        // then
        assertThat(result).isNull();
    }
}