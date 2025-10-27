package com.meetup.hereandnow.scrap.application.service;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.scrap.domain.CourseScrap;
import com.meetup.hereandnow.scrap.dto.response.ScrapResponseDto;
import com.meetup.hereandnow.scrap.exception.ScrapErrorCode;
import com.meetup.hereandnow.scrap.repository.CourseScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseScrapService {

    private final CourseScrapRepository courseScrapRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public Optional<CourseScrap> findOptional(Member member, Long courseId) {
        return courseScrapRepository.findByMemberIdAndCourseId(member.getId(), courseId);
    }

    @Transactional
    public ScrapResponseDto scrap(Member member, Long courseId) {
        Course course = courseRepository.findByIdWithLock(courseId)
                .orElseThrow(ScrapErrorCode.COURSE_NOT_FOUND::toException);

        Optional<CourseScrap> existingScrap =
                courseScrapRepository.findByMemberIdAndCourseId(member.getId(), courseId);
        if (existingScrap.isPresent()) {
            return ScrapResponseDto.from(existingScrap.get());
        }

        CourseScrap scrap = CourseScrap.builder()
                .member(member)
                .course(course)
                .build();
        course.incrementScrapCount();
        courseScrapRepository.save(scrap);
        return ScrapResponseDto.from(scrap);
    }

    @Transactional
    public ScrapResponseDto deleteScrap(CourseScrap courseScrap) {
        Course course = courseRepository.findByIdWithLock(courseScrap.getCourse().getId())
                .orElseThrow(ScrapErrorCode.COURSE_NOT_FOUND::toException);
        course.decrementScrapCount();
        courseScrapRepository.delete(courseScrap);
        return ScrapResponseDto.from();
    }
}
