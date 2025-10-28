package com.meetup.hereandnow.course.application.service.save.course;

import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.core.util.UUIDUtils;
import com.meetup.hereandnow.course.application.service.save.couple.CoupleCoursePersistService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.dto.request.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import com.meetup.hereandnow.course.dto.response.CourseSaveResponseDto;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.dto.PinDirnameDto;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseSaveService {

    private final CourseRedisService courseRedisService;
    private final CoursePersistService coursePersistService;
    private final CoupleCoursePersistService coupleCoursePersistService;

    public CourseSaveResponseDto saveCourseToRedis(
            CourseSaveDto courseSaveDto
    ) {

        Member member = SecurityUtils.getCurrentMember();

        String courseUUID = UUIDUtils.getUUID();
        String courseDirname = String.format("course/%s/image", courseUUID);

        List<PinDirnameDto> pinDirs = createPinDirnames(courseSaveDto.pinList(), courseUUID);
        courseRedisService.saveCourse(member, courseUUID, courseSaveDto);

        return new CourseSaveResponseDto(courseUUID, courseDirname, pinDirs);
    }

    @Transactional
    public Long commitSave(
            String courseUuid,
            CommitSaveCourseRequestDto commitSaveCourseRequestDto
    ) {
        Member member = SecurityUtils.getCurrentMember();

        CourseSaveDto dto = courseRedisService.getCourse(member, courseUuid);
        if (dto == null) {
            throw CourseErrorCode.NOT_FOUND_COURSE_METADATA.toException();
        }

        Course course = coursePersistService.persist(dto, member, commitSaveCourseRequestDto);

        coupleCoursePersistService.coupleCourseSavePersist(dto, member, course, commitSaveCourseRequestDto);

        courseRedisService.deleteCourse(member, courseUuid);

        return course.getId();
    }

    private List<PinDirnameDto> createPinDirnames(
            List<PinSaveDto> pins, String uuid
    ) {

        List<PinDirnameDto> dirs = new ArrayList<>();
        for (int i = 0; i < pins.size(); i++) {
            dirs.add(new PinDirnameDto(i, String.format("course/%s/pins/%s/images", uuid, UUIDUtils.getUUID())));
        }
        return dirs;
    }
}
