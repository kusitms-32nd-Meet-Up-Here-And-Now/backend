package com.meetup.hereandnow.course.application.facade;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.course.application.service.save.course.CourseSaveService;
import com.meetup.hereandnow.course.dto.request.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import com.meetup.hereandnow.course.dto.response.CommitSaveCourseResponseDto;
import com.meetup.hereandnow.course.dto.response.CourseSaveResponseDto;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.pin.dto.PinImageObjectKeyDto;
import com.meetup.hereandnow.pin.exception.PinErrorCode;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
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
        validateImagesExist(requestDto);
        validatePinImagesExist(requestDto.pinImageObjectKeyList(), requestDto.coupleCourseImageObjectKeyList());

        Long savedCourseId = courseSaveService.commitSave(courseUuid, requestDto);

        return CommitSaveCourseResponseDto.of(savedCourseId);
    }

    private void validateImagesExist(CommitSaveCourseRequestDto requestDto) {
        if (!objectStorageService.exists(requestDto.courseImageObjectKey())) {
            throw CourseErrorCode.NOT_FOUND_COURSE_IMAGE.toException();
        }

        if(requestDto.coupleCourseImageObjectKeyList() != null) {
            validateCoupleCourseImage(requestDto.coupleCourseImageObjectKeyList());
        }
    }

    private void validatePinImagesExist(List<PinImageObjectKeyDto> pinImageObjectKeyDtoList, List<String> coupleImageObjectKeyList) {
        Stream<String> pinImageKeys = pinImageObjectKeyDtoList.stream()
                .map(PinImageObjectKeyDto::objectKeyList)
                .flatMap(Collection::stream);

        Stream<String> coupleImageKeys = coupleImageObjectKeyList != null ? coupleImageObjectKeyList.stream() : Stream.empty();

        Stream.concat(pinImageKeys, coupleImageKeys)
                .parallel()
                .forEach(key -> {
                    if (!objectStorageService.exists(key)) {
                        throw PinErrorCode.NOT_FOUND_PIN_IMAGE.toException();
                    }
                });
    }

    private void validateCoupleCourseImage(List<String> coupleCourseImageObjectKeyList) {
        coupleCourseImageObjectKeyList.stream()
                .parallel()
                .forEach(key -> {
                    if(!objectStorageService.exists(key)){
                        throw CourseErrorCode.NOT_FOUND_COURSE_IMAGE.toException();
                    }
                });
    }
}
