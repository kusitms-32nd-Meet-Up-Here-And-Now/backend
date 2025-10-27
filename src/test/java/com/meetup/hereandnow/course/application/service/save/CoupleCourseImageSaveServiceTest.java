package com.meetup.hereandnow.course.application.service.save;

import com.meetup.hereandnow.course.application.service.save.couple.CoupleCourseImageSaveService;
import com.meetup.hereandnow.course.domain.entity.CoupleCourseRecord;
import com.meetup.hereandnow.course.dto.request.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.course.infrastructure.repository.CoupleCourseImageRepository;
import java.util.List;
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
class CoupleCourseImageSaveServiceTest {

    @Mock
    private CoupleCourseImageRepository coupleCourseImageRepository;

    @InjectMocks
    private CoupleCourseImageSaveService coupleCourseImageSaveService;

    private CoupleCourseRecord coupleCourseRecord;

    @BeforeEach
    void setUp() {
        coupleCourseRecord = CoupleCourseRecord.builder().id(1L).build();
    }

    @Test
    @DisplayName("커플 코스 기록 이미지가 정상적으로 저장된다.")
    void success_couple_course_image_save() {

        // given
        CommitSaveCourseRequestDto commitSaveCourseRequestDto = new CommitSaveCourseRequestDto(
                null, List.of("/course/1/1.jpg"), null
        );

        // when
        coupleCourseImageSaveService.saveCoupleCourseImage(coupleCourseRecord, commitSaveCourseRequestDto);

        // then
        verify(coupleCourseImageRepository).saveAll(anyList());
        assertThat(coupleCourseRecord.getCoupleCourseImages()).hasSize(1);
    }

    @Test
    @DisplayName("커플 코스 기록 이미지가 없는 경우 저장되지 않는다.")
    void fail_no_images_in_list() {
        // given
        CommitSaveCourseRequestDto commitSaveCourseRequestDto = new CommitSaveCourseRequestDto(
                null, null, null
        );

        // when
        coupleCourseImageSaveService.saveCoupleCourseImage(coupleCourseRecord, commitSaveCourseRequestDto);

        // then
        verify(coupleCourseImageRepository, never()).saveAll(anyList());
    }
}