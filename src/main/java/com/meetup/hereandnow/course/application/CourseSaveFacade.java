package com.meetup.hereandnow.course.application;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.course.dto.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import com.meetup.hereandnow.course.dto.response.CourseSaveResponseDto;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.pin.application.PinSaveFacade;
import com.meetup.hereandnow.pin.dto.PinImageObjectKeyDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CourseSaveFacade {

    private final ObjectStorageService objectStorageService;
    private final CourseSaveService courseSaveService;
    private final PinSaveFacade pinSaveFacade;

    public CourseSaveResponseDto prepareCourseSave(CourseSaveDto requestDto) {
        return courseSaveService.saveCourseToRedis(requestDto);
    }

    @Transactional
    public void commitSaveCourse(CommitSaveCourseRequestDto requestDto) {
        validateImagesExist(requestDto);

        pinSaveFacade.validatePinImagesExist(requestDto.pinImageObjectKeyList());

        courseSaveService.saveCourseAndRelatedEntities(requestDto);
    }

    private void validateImagesExist(CommitSaveCourseRequestDto requestDto) {
        if (!objectStorageService.exists(requestDto.courseImageObjectKey())) {
            throw CourseErrorCode.NOT_FOUND_COURSE_IMAGE.toException();
        }
    }
}
