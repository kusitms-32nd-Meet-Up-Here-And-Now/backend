package com.meetup.hereandnow.archive.application.service;

import com.meetup.hereandnow.archive.application.service.converter.CourseCardDtoConverterService;
import com.meetup.hereandnow.archive.dto.response.CourseCardDto;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.scrap.domain.CourseScrap;
import com.meetup.hereandnow.scrap.infrastructure.repository.CourseScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArchiveCourseService {

    private final CourseRepository courseRepository;
    private final CourseScrapRepository courseScrapRepository;
    private final CourseCardDtoConverterService converterService;

    public List<CourseCardDto> getMyScrappedCourses(Member member, PageRequest pageRequest) {
        Page<CourseScrap> scrapPage = courseScrapRepository
                .findByMemberWithCourse(member, pageRequest);
        List<Course> courses = scrapPage.stream()
                .map(CourseScrap::getCourse)
                .toList();
        if (courses.isEmpty()) {
            return Collections.emptyList();
        }
        return converterService.convertToCourseCardDto(courses);
    }

    public List<CourseCardDto> getMyCreatedCourses(Member member, PageRequest pageRequest) {
        Page<Course> coursePage = courseRepository.findByMemberOrderByCreatedAtDesc(member, pageRequest);
        List<Course> courses = coursePage.getContent();
        if (courses.isEmpty()) {
            return Collections.emptyList();
        }
        return converterService.convertToCourseCardDto(courses);
    }
}
