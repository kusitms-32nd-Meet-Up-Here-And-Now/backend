package com.meetup.hereandnow.course.application;

import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.core.util.UUIDUtils;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import com.meetup.hereandnow.course.dto.response.CourseSaveResponseDto;
import com.meetup.hereandnow.course.infrastructure.redis.CourseRedis;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.dto.PinDirnameDto;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseSaveService {

    private final CourseRedis courseRedis;

    public CourseSaveResponseDto courseSave(CourseSaveDto courseSaveDto){

        Member member = SecurityUtils.getCurrentMember();
        String courseUUID = UUIDUtils.getUUID();

        String courseDirname = String.format("/course/%s/images", courseUUID);

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
}
