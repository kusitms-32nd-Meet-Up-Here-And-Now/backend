package com.meetup.hereandnow.archive.application.facade;

import com.meetup.hereandnow.archive.application.service.ArchiveCourseService;
import com.meetup.hereandnow.archive.dto.response.CourseFolderResponseDto;
import com.meetup.hereandnow.archive.dto.response.RecentArchiveResponseDto;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArchiveFacade {

    private final ArchiveCourseService archiveCourseService;

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
        Page<Course> coursePage = archiveCourseService.getCoursePageByMember(
                SecurityUtils.getCurrentMember(),
                PageRequest.of(page, size)
        );
        if (coursePage.hasContent()) {
            List<Course> courses = coursePage.getContent();
            return courses.stream().map(CourseFolderResponseDto::from).toList();
        } else {
            return Collections.emptyList();
        }
    }
}
