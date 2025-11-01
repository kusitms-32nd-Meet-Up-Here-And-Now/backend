package com.meetup.hereandnow.course.application.service.save.course;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CourseTagSaveService {

    public void saveCourseTag(Course course, List<PinSaveDto> pinSaveDtoList) {
        List<String> courseTags = getCourseTag(pinSaveDtoList);
        course.updateTags(courseTags);
    }

    private List<String> getCourseTag(List<PinSaveDto> pinTagDtoList) {
        return pinTagDtoList.stream()
                .flatMap(pinSaveDto -> {
                    List<String> tagNames = pinSaveDto.pinTagNames();
                    if (tagNames == null || tagNames.isEmpty()) {
                        return Stream.empty();
                    }
                    return tagNames.stream();
                })
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(2)
                .map(Map.Entry::getKey)
                .toList();
    }
}
