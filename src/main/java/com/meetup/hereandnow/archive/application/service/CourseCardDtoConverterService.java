package com.meetup.hereandnow.archive.application.service;

import com.meetup.hereandnow.archive.dto.response.CourseCardDto;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.domain.entity.CourseTag;
import com.meetup.hereandnow.course.infrastructure.repository.CourseTagRepository;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.domain.entity.PinImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseCardDtoConverterService {

    private final CourseTagRepository courseTagRepository;

    public List<CourseCardDto> convertToCourseCardDto(List<Course> courses) {
        if (courses == null || courses.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> courseIds = courses.stream().map(Course::getId).toList();
        Map<Long, List<String>> courseTagMap = getCourseTagMap(courseIds);
        return courses.stream().map(c ->
                toCourseCardDto(c, courseTagMap.getOrDefault(c.getId(), Collections.emptyList()))
        ).toList();
    }

    private Map<Long, List<String>> getCourseTagMap(List<Long> courseIds) {
        List<CourseTag> allTags = courseTagRepository.findAllByCourseIdIn(courseIds);
        return allTags.stream().collect(Collectors.groupingBy(
                tag -> tag.getCourse().getId(),
                Collectors.mapping(
                        tag -> tag.getCourseTagName().getName(),
                        Collectors.toList()
                )
        ));
    }

    private CourseCardDto toCourseCardDto(Course course, List<String> tagNames) {
        return new CourseCardDto(
                course.getId(),
                course.getCourseTitle(),
                course.getCourseDescription(),
                tagNames,
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
