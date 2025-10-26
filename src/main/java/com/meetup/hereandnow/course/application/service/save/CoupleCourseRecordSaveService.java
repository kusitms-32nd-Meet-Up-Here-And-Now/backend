package com.meetup.hereandnow.course.application.service.save;

import com.meetup.hereandnow.course.domain.entity.CoupleCourseRecord;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.request.CoupleCourseRecordSaveRequestDto;
import com.meetup.hereandnow.course.infrastructure.repository.CoupleCourseRecordRepository;
import com.meetup.hereandnow.member.domain.Couple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoupleCourseRecordSaveService {

    private final CoupleCourseRecordRepository coupleCourseRecordRepository;

    public CoupleCourseRecord coupleCourseRecords(
            CoupleCourseRecordSaveRequestDto coupleCourseRecordSaveRequestDto,
            Course course,
            Couple couple
    ) {
        if (coupleCourseRecordSaveRequestDto == null) {
            return null;
        }

        CoupleCourseRecord coupleCourseRecord = CoupleCourseRecord.builder()
                .descriptionByGirlfriend(coupleCourseRecordSaveRequestDto.descriptionByGirlfriend())
                .descriptionByBoyfriend(coupleCourseRecordSaveRequestDto.descriptionByBoyfriend())
                .course(course)
                .couple(couple)
                .build();

        return coupleCourseRecordRepository.save(coupleCourseRecord);
    }
}
