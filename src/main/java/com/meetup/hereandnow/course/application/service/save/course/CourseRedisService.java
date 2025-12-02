package com.meetup.hereandnow.course.application.service.save.course;

import com.meetup.hereandnow.course.dto.request.CourseSaveDto;
import com.meetup.hereandnow.course.infrastructure.redis.CourseRedis;
import com.meetup.hereandnow.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseRedisService {
    private final CourseRedis courseRedis;

    public void saveCourse(Member member, String uuid, CourseSaveDto dto) {
        courseRedis.saveCourseKey(member.getId(), uuid, dto);
    }

    public CourseSaveDto getCourse(Member member, String uuid) {
        return courseRedis.getCourseDto(member.getId(), uuid);
    }

    public void deleteCourse(Member member, String uuid) {
        courseRedis.deleteCourseKey(member.getId(), uuid);
    }
}
