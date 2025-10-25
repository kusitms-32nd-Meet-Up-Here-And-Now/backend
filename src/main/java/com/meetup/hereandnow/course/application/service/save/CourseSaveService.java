package com.meetup.hereandnow.course.application.service.save;

import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.core.util.UUIDUtils;
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

    private final CourseRedisService redisService;
    private final CoursePersistService persistService;

    public CourseSaveResponseDto saveCourseToRedis(
            CourseSaveDto courseSaveDto
    ) {

        Member member = SecurityUtils.getCurrentMember();

        String courseUUID = UUIDUtils.getUUID();
        String courseDirname = String.format("/course/%s/image", courseUUID);

        List<PinDirnameDto> pinDirs = createPinDirnames(courseSaveDto.pinList(), courseUUID);
        redisService.saveCourse(member, courseUUID, courseSaveDto);

        return new CourseSaveResponseDto(courseUUID, courseDirname, pinDirs);
    }

    @Transactional
    public Long commitSave(
            String courseUuid,
            CommitSaveCourseRequestDto commitSaveCourseRequestDto
    ) {
        Member member = SecurityUtils.getCurrentMember();

        CourseSaveDto dto = redisService.getCourse(member, courseUuid);
        if (dto == null) throw CourseErrorCode.NOT_FOUND_COURSE_METADATA.toException();

        Long courseId = persistService.persist(dto, member, commitSaveCourseRequestDto);
        redisService.deleteCourse(member, courseUuid);

        return courseId;
    }

    private List<PinDirnameDto> createPinDirnames(
            List<PinSaveDto> pins, String uuid
    ) {

        List<PinDirnameDto> dirs = new ArrayList<>();
        for (int i = 0; i < pins.size(); i++) {
            dirs.add(new PinDirnameDto(i, String.format("/course/%s/pins/%s/images", uuid, UUIDUtils.getUUID())));
        }
        return dirs;
    }
}
