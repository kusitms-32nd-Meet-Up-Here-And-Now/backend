package com.meetup.hereandnow.course.application.service.view;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.response.CourseCardResponseDto;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseCardDtoConverter {

    private final ObjectStorageService objectStorageService;

    public List<CourseCardResponseDto> convert(List<Course> courses) {
        if (courses.isEmpty()) {
            return Collections.emptyList();
        }
        return courses.stream()
                .map(course -> CourseCardResponseDto.from(course, getCourseImages(course)))
                .toList();
    }

    private List<String> getCourseImages(Course course) {
        return course.getPinList().stream()
                .map(pin -> pin.getPinImages().stream().findFirst())
                .flatMap(Optional::stream)
                .map(PinImage::getImageUrl)
                .map(objectStorageService::buildImageUrl)
                .toList();
    }
}
