package com.meetup.hereandnow.course.application;

import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.core.util.UUIDUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.domain.entity.CourseTag;
import com.meetup.hereandnow.course.domain.value.CourseTagEnum;
import com.meetup.hereandnow.course.dto.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import com.meetup.hereandnow.course.dto.response.CourseSaveResponseDto;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.course.infrastructure.redis.CourseRedis;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.course.infrastructure.repository.CourseTagRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.application.PinSaveFacade;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.pin.dto.PinDirnameDto;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import com.meetup.hereandnow.place.application.PlaceSaveFacade;
import com.meetup.hereandnow.place.domain.Place;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CourseSaveService {

    private final CourseRedis courseRedis;
    private final PlaceSaveFacade placeSaveFacade;
    private final PinSaveFacade pinSaveFacade;
    private final CourseRepository courseRepository;
    private final CourseTagRepository courseTagRepository;

    public CourseSaveResponseDto saveCourseToRedis(CourseSaveDto courseSaveDto) {

        Member member = SecurityUtils.getCurrentMember();
        String courseUUID = UUIDUtils.getUUID();

        String courseDirname = String.format("/course/%s/image", courseUUID);

        List<PinSaveDto> pinList = courseSaveDto.pinList();
        List<PinDirnameDto> pinDirnameList = new ArrayList<>();
        for (int i = 0; i < pinList.size(); i++) {
            String pinDirname = String.format("/course/%s/pins/%s/images",
                    courseUUID,
                    UUIDUtils.getUUID());
            pinDirnameList.add(new PinDirnameDto(i, pinDirname));
        }

        courseRedis.saveCourseKey(member.getId(), courseUUID, courseSaveDto);

        return new CourseSaveResponseDto(courseUUID, courseDirname, pinDirnameList);
    }

    public void saveCourseAndRelatedEntities(CommitSaveCourseRequestDto commitSaveCourseRequestDto) {
        Member member = SecurityUtils.getCurrentMember();

        var courseSaveDto = courseRedis.getCourseDto(member.getId(), commitSaveCourseRequestDto.courseUuid());
        if (courseSaveDto == null) {
            throw CourseErrorCode.NOT_FOUND_COURSE_METADATA.toException();
        }

        // 1) Course 생성 및 저장
        Course course = createCourse(courseSaveDto, member);
        courseRepository.save(course);

        List<CourseTag> courseTagList = createCourseTag(courseSaveDto.courseTagList(), course);
        courseTagRepository.saveAll(courseTagList);

        // 2) Place 처리(중복 재사용 + batch 저장)는 PlaceSaveFacade에 위임
        Map<String, Place> placeMap = placeSaveFacade.findOrCreatePlaces(courseSaveDto.pinList());
        // TODO: placeTag 삽입 구현
        // 3) Pin 생성/배치 저장은 PinSaveFacade에 위임
        List<Pin> savedPins = pinSaveFacade.savePins(courseSaveDto.pinList(), course, placeMap);

        // 4) Redis 키 삭제
        courseRedis.deleteCourseKey(member.getId(), commitSaveCourseRequestDto.courseUuid());
    }

    private Course createCourse(CourseSaveDto dto, Member member) {
        return Course.builder()
                .courseTitle(dto.courseTitle())
                .courseRating(BigDecimal.valueOf(dto.courseRating()))
                .courseDescription(dto.courseDescription())
                .isPublic(dto.isPublic())
                .member(member)
                .build();
    }

    private List<CourseTag> createCourseTag(List<CourseTagEnum> courseTagEnumList, Course course) {
        return courseTagEnumList.stream()
                .map(tagEnum -> CourseTag.builder()
                        .courseTagName(tagEnum)
                        .course(course)
                        .build())
                .collect(Collectors.toList());
    }
}
