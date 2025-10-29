package com.meetup.hereandnow.archive.application.facade;

import com.meetup.hereandnow.archive.application.service.ArchiveCourseService;
import com.meetup.hereandnow.archive.dto.response.CourseCardDto;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArchiveFacade {

    private final ArchiveCourseService archiveCourseService;

    @Transactional(readOnly = true)
    public List<CourseCardDto> getMyScrappedCourses(int page, int size) {
        Member member = SecurityUtils.getCurrentMember();
        PageRequest pageRequest = PageRequest.of(page, size);
        return archiveCourseService.getMyScrappedCourses(member, pageRequest);
    }

    @Transactional(readOnly = true)
    public List<CourseCardDto> getMyCreatedCourses(int page, int size) {
        Member member = SecurityUtils.getCurrentMember();
        PageRequest pageRequest = PageRequest.of(page, size);
        return archiveCourseService.getMyCreatedCourses(member, pageRequest);
    }
}
