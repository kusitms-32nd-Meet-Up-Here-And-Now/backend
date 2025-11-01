package com.meetup.hereandnow.archive.application.service;

import com.meetup.hereandnow.archive.dto.response.CourseCardDto;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseCardDtoConverterService {

    public List<CourseCardDto> convertToCourseCardDto(List<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            return Collections.emptyList();
        }
        return courses.stream().map(this::toCourseCardDto).toList();
    }

    private CourseCardDto toCourseCardDto(Course course) {
        return new CourseCardDto(
                course.getId(),
                course.getCourseTitle(),
                course.getCourseDescription(),
                course.getCourseTags(),
                course.getViewCount(),
                course.getCourseRating().doubleValue(),
                getFirstPinImageUrls(course.getPinList())
        );
    }

    private List<String> getFirstPinImageUrls(List<Pin> pins) {
        return pins.stream()
                .map(this::findFirstPinImageUrl)
                .flatMap(Optional::stream)
                .toList();
    }

    private Optional<String> findFirstPinImageUrl(Pin pin) {
        return pin.getPinImages().stream()
                .min(Comparator.comparing(PinImage::getId))
                .map(PinImage::getImageUrl);
    }
}
