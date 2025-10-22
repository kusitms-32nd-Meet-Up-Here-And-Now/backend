package com.meetup.hereandnow.course.application.service.save;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.request.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import com.meetup.hereandnow.course.infrastructure.mapper.CourseMapper;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.application.facade.PinSaveFacade;
import com.meetup.hereandnow.place.application.facade.PlaceSaveFacade;
import com.meetup.hereandnow.place.domain.Place;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoursePersistService {

    private final CourseRepository courseRepository;
    private final CourseTagService courseTagService;
    private final PlaceSaveFacade placeSaveFacade;
    private final PinSaveFacade pinSaveFacade;

    public Long persist(CourseSaveDto dto, Member member, CommitSaveCourseRequestDto commitSaveCourseRequestDto) {
        Course course = CourseMapper.toEntity(dto, member, commitSaveCourseRequestDto.courseImageObjectKey());
        courseRepository.save(course);

        courseTagService.saveTags(dto.courseTagList(), course);

        Map<String, Place> placeMap = placeSaveFacade.findOrCreatePlaces(dto.pinList());
        pinSaveFacade.savePinEntityToTable(
                dto.pinList(), course, placeMap, commitSaveCourseRequestDto);

        return course.getId();
    }
}
