package com.meetup.hereandnow.scrap.application.service;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.scrap.domain.CourseScrap;
import com.meetup.hereandnow.scrap.dto.response.ScrapResponseDto;
import com.meetup.hereandnow.scrap.exception.ScrapErrorCode;
import com.meetup.hereandnow.scrap.infrastructure.repository.CourseScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseScrapService {

    private final CourseScrapRepository courseScrapRepository;
    private final CourseRepository courseRepository;

    /**
     * CourseScrap을 생성 또는 삭제합니다.
     */
    public ScrapResponseDto toggleScrapCourse(Member member, Long courseId) {

        Course course = courseRepository.findByIdWithLock(courseId)
                .orElseThrow(ScrapErrorCode.PLACE_NOT_FOUND::toException);

        Optional<CourseScrap> optionalScrap =
                courseScrapRepository.findByMemberIdAndCourseId(member.getId(), courseId);

        if (optionalScrap.isEmpty()) {
            CourseScrap scrap = CourseScrap.builder()
                    .member(member)
                    .course(course)
                    .build();
            course.incrementScrapCount();
            courseScrapRepository.save(scrap);
            return ScrapResponseDto.from(scrap);

        } else {
            course.decrementScrapCount();
            courseScrapRepository.delete(optionalScrap.get());
            return ScrapResponseDto.from();
        }
    }
}
