package com.meetup.hereandnow.course.application.facade;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.course.application.service.save.CourseSaveService;
import com.meetup.hereandnow.course.dto.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import com.meetup.hereandnow.course.dto.response.CourseSaveResponseDto;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.pin.dto.PinImageObjectKeyDto;
import com.meetup.hereandnow.pin.exception.PinErrorCode;
import java.util.Collection;
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
    public void commitSaveCourse(CommitSaveCourseRequestDto requestDto) {
        validateImagesExist(requestDto);
        validatePinImagesExist(requestDto.pinImageObjectKeyList());

        courseSaveService.commitSave(requestDto);
    }

    private void validateImagesExist(CommitSaveCourseRequestDto requestDto) {
        if (!objectStorageService.exists(requestDto.courseImageObjectKey())) {
            throw CourseErrorCode.NOT_FOUND_COURSE_IMAGE.toException();
        }
    }

    private void validatePinImagesExist(List<PinImageObjectKeyDto> pinImageObjectKeyDtoList) {
        pinImageObjectKeyDtoList.stream()
                .map(PinImageObjectKeyDto::objectKeyList)
                .flatMap(Collection::stream)
                .parallel()
                .forEach(key -> {
                    if (!objectStorageService.exists(key)) {
                        throw PinErrorCode.NOT_FOUND_PIN_IMAGE.toException();
                    }
                });
    }
}
