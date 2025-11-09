package com.meetup.hereandnow.course.application.facade;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.course.application.service.save.course.CourseSaveService;
import com.meetup.hereandnow.course.dto.request.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.course.dto.request.CourseSaveDto;
import com.meetup.hereandnow.course.dto.response.CommitSaveCourseResponseDto;
import com.meetup.hereandnow.course.dto.response.CourseSaveResponseDto;
import com.meetup.hereandnow.pin.dto.PinImageObjectKeyDto;
import com.meetup.hereandnow.pin.exception.PinErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseSaveFacade {

    private final ObjectStorageService objectStorageService;
    private final CourseSaveService courseSaveService;

    public CourseSaveResponseDto prepareCourseSave(CourseSaveDto requestDto) {
        return courseSaveService.saveCourseToRedis(requestDto);
    }

    @Transactional
    public CommitSaveCourseResponseDto commitSaveCourse(
            String courseUuid,
            CommitSaveCourseRequestDto requestDto
    ) {
        validatePinImagesExist(requestDto.pinImageObjectKeyList());

        Long savedCourseId = courseSaveService.commitSave(courseUuid, requestDto);

        return CommitSaveCourseResponseDto.of(savedCourseId);
    }

    private void validatePinImagesExist(List<PinImageObjectKeyDto> pinImageObjectKeyDtoList) {
        List<String> objectKeyList = pinImageObjectKeyDtoList.stream()
                .map(PinImageObjectKeyDto::objectKeyList)
                .flatMap(List::stream)
                .toList();

        objectKeyList.stream()
                .parallel()
                .forEach(key -> {
                    if (!objectStorageService.exists(key)) {
                        throw PinErrorCode.NOT_FOUND_PIN_IMAGE.toException();
                    }
                });
    }
}
