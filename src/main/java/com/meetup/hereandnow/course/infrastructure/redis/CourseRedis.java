package com.meetup.hereandnow.course.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetup.hereandnow.course.domain.value.CourseKeyPrefix;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CourseRedis {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void saveCourseKey(Long memberId, String courseUUID, CourseSaveDto courseSaveDto) {
        redisTemplate.opsForValue().set(getCourseKey(memberId, courseUUID), courseSaveDto, Duration.ofMinutes(5));
    }

    public void deleteCourseKey(Long memberId, String courseUUID) {
        redisTemplate.delete(getCourseKey(memberId, courseUUID));
    }

    public CourseSaveDto getCourseDto(Long memberId, String courseUUID) {
        return objectMapper.convertValue(
                redisTemplate.opsForValue().get(getCourseKey(memberId, courseUUID)),
                CourseSaveDto.class
        );
    }

    private String getCourseKey(Long memberId, String courseUUID) {
        return CourseKeyPrefix.COURSE_KEY_PREFIX.key(memberId +":"+courseUUID);
    }
}
