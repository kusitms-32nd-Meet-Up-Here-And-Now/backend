package com.meetup.hereandnow.course.application.service.save.course;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import com.meetup.hereandnow.course.dto.request.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.course.infrastructure.mapper.CourseMapper;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.application.facade.PinSaveFacade;
import com.meetup.hereandnow.place.application.facade.PlaceSaveFacade;
import com.meetup.hereandnow.place.domain.Place;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CoursePersistService {

    private final CourseRepository courseRepository;
    private final PlaceSaveFacade placeSaveFacade;
    private final PinSaveFacade pinSaveFacade;

    public Course persist(CourseSaveDto dto, Member member, CommitSaveCourseRequestDto commitSaveCourseRequestDto) {
        Course course = CourseMapper.toEntity(dto, member);
        courseRepository.save(course);

        Map<String, Place> placeMap = placeSaveFacade.findOrCreatePlaces(dto.pinList());
        pinSaveFacade.savePinEntityToTable(
                dto.pinList(), course, placeMap, commitSaveCourseRequestDto
        );

        return course;
    }
}
