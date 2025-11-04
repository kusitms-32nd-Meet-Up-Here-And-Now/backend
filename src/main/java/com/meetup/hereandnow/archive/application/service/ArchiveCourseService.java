package com.meetup.hereandnow.archive.application.service;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.pin.infrastructure.repository.PinImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArchiveCourseService {

    private final CourseRepository courseRepository;
    private final PinImageRepository pinImageRepository;
    private final ObjectStorageService objectStorageService;

    public Optional<Course> getRecentCourseByMember(Member member) {
        return courseRepository.findByMemberOrderByCreatedAtDesc(member);
    }

    public List<String> getCourseImages(Long courseId) {
        List<String> courseImages = pinImageRepository.findImageUrlsByCourseId(courseId);
        if (!courseImages.isEmpty()) {
            Collections.shuffle(courseImages);
            courseImages = courseImages.stream()
                    .map(objectStorageService::buildImageUrl)
                    .limit(3)
                    .toList();
        }
        return courseImages;
    }

    public Page<Course> getCoursePageByMember(Member member, PageRequest pageRequest) {
        return courseRepository.findByMemberOrderByCreatedAtDesc(member, pageRequest);
    }
}
