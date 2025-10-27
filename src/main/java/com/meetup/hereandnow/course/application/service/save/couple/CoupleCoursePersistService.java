package com.meetup.hereandnow.course.application.service.save.couple;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import com.meetup.hereandnow.course.dto.request.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.course.dto.request.CoupleCourseRecordSaveRequestDto;
import com.meetup.hereandnow.member.domain.Couple;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.exception.CoupleErrorCode;
import com.meetup.hereandnow.member.repository.CoupleRepository;
import com.meetup.hereandnow.pin.application.service.save.CouplePinImageSaveService;
import com.meetup.hereandnow.pin.application.service.save.CouplePinRecordSaveService;
import com.meetup.hereandnow.pin.domain.entity.CouplePinRecord;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoupleCoursePersistService {

    private final CoupleCourseImageSaveService coupleCourseImageSaveService;
    private final CoupleCourseRecordSaveService coupleCourseRecordSaveService;
    private final CouplePinRecordSaveService couplePinRecordSaveService;

    private final CoupleRepository coupleRepository;
    private final CouplePinImageSaveService couplePinImageSaveService;

    public void coupleCourseSavePersist(
            CourseSaveDto courseSaveDto,
            Member member,
            Course course,
            CommitSaveCourseRequestDto commitSaveCourseRequestDto
    ) {
        CoupleCourseRecordSaveRequestDto dto = courseSaveDto.coupleCourseRecordSaveRequestDto();
        if (dto == null) {
            return;
        }

        Couple couple = getCouple(member);

        var result = coupleCourseRecordSaveService.saveCoupleCourseRecords(dto, course, couple);
        coupleCourseImageSaveService.saveCoupleCourseImage(result, commitSaveCourseRequestDto);

        List<Pin> pinList = course.getPinList();
        List<CouplePinRecord> couplePinRecords = couplePinRecordSaveService.saveCouplePinRecords(
                pinList,
                courseSaveDto.pinList(),
                couple
        );

        couplePinImageSaveService.saveCouplePinImages(
                couplePinRecords, commitSaveCourseRequestDto.pinImageObjectKeyList()
        );
    }

    private Couple getCouple(Member member) {
        return coupleRepository.findByMember(member)
                .orElseThrow(CoupleErrorCode.NOT_FOUND_COUPLE::toException);
    }
}
