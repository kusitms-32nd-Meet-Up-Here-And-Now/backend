package com.meetup.hereandnow.course.application.service.save;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.domain.entity.CourseTag;
import com.meetup.hereandnow.course.domain.value.CourseTagEnum;
import com.meetup.hereandnow.course.infrastructure.repository.CourseTagRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseTagService {
    private final CourseTagRepository courseTagRepository;

    public void saveTags(List<CourseTagEnum> tagEnums, Course course) {
        List<CourseTag> tags = tagEnums.stream()
                .map(tagEnum -> CourseTag.of(tagEnum, course))
                .collect(Collectors.toList());
        courseTagRepository.saveAll(tags);
    }
}
