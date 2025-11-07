package com.meetup.hereandnow.archive.application.facade;

import com.meetup.hereandnow.archive.application.service.ArchiveCourseService;
import com.meetup.hereandnow.archive.dto.response.CourseFolderResponseDto;
import com.meetup.hereandnow.archive.dto.response.RecentArchiveResponseDto;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.application.service.search.CourseSearchService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArchiveFacade {

    private final ArchiveCourseService archiveCourseService;
    private final CourseSearchService courseSearchService;

    @Transactional(readOnly = true)
    public RecentArchiveResponseDto getRecentArchive() {
        Member member = SecurityUtils.getCurrentMember();
        Optional<Course> course = archiveCourseService.getRecentCourseByMember(member);
        return course.map(c -> RecentArchiveResponseDto.from(
                c,
                archiveCourseService.getCourseImages(c.getId())
        )).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<CourseFolderResponseDto> getMyCreatedCourses(int page, int size) {
        Page<Long> idPage = archiveCourseService.getCourseIdsByMember(
                SecurityUtils.getCurrentMember(),
                PageRequest.of(page, size)
        );
        if (!idPage.hasContent()) {
            return Collections.emptyList();
        }
        List<Course> courses = archiveCourseService.getCoursesWithPins(idPage.getContent());
        return courses.stream().map(CourseFolderResponseDto::from).toList();
    }

    @Transactional(readOnly = true)
    public List<CourseFolderResponseDto> getFilteredArchiveCourses(
            int page,
            int size,
            Integer rating,
            List<String> keyword,
            LocalDate startDate,
            LocalDate endDate,
            String with,
            String region,
            List<String> placeCode,
            List<String> tag
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Member member = SecurityUtils.getCurrentMember();
        Page<Course> coursePage = courseSearchService.searchCoursesByMember(
                member, rating, keyword, startDate, endDate, with, region, placeCode, tag, pageRequest
        );
        if (coursePage.hasContent()) {
            return coursePage.getContent().stream().map(CourseFolderResponseDto::from).toList();
        } else return Collections.emptyList();
    }
}
