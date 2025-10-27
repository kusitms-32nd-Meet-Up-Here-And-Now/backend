package com.meetup.hereandnow.course.application.service.save.couple;

import com.meetup.hereandnow.course.domain.entity.CoupleCourseImage;
import com.meetup.hereandnow.course.domain.entity.CoupleCourseRecord;
import com.meetup.hereandnow.course.dto.request.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.course.infrastructure.repository.CoupleCourseImageRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoupleCourseImageSaveService {

    private final CoupleCourseImageRepository coupleCourseImageRepository;

    public void saveCoupleCourseImage(
            CoupleCourseRecord coupleCourseRecord,
            CommitSaveCourseRequestDto commitSaveCourseRequestDto
    ) {
        List<String> coupleCourseImageKeyList = commitSaveCourseRequestDto.coupleCourseImageObjectKeyList();

        if (coupleCourseImageKeyList == null || coupleCourseImageKeyList.isEmpty()) {
            return;
        }

        List<CoupleCourseImage> coupleCourseImageList = coupleCourseImageKeyList.stream()
                .map(imageUrl -> {
                    CoupleCourseImage coupleCourseImage = CoupleCourseImage.of(imageUrl, coupleCourseRecord);
                    coupleCourseRecord.addCoupleCourseImage(coupleCourseImage);
                    return coupleCourseImage;
                })
                .toList();

        coupleCourseImageRepository.saveAll(coupleCourseImageList);
    }
}
