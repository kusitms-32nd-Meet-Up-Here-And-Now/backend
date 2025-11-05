package com.meetup.hereandnow.course.application.service.search;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.course.infrastructure.specification.CourseSpecifications;
import com.meetup.hereandnow.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseSearchService {

    private final CourseRepository courseRepository;

    public Page<Course> searchCourses(
            Member member,
            Integer rating,
            List<String> keywords,
            LocalDate date,
            String with,
            String region,
//            List<String> placeCodes,
            List<String> tags,
            Pageable pageable
    ) {
        Specification<Course> spec = Specification.where(CourseSpecifications.hasMember(member));

        if (rating != null && rating > 0) {
            spec = spec.and(CourseSpecifications.isRatingInRange(rating));
        }

        if (keywords != null && !keywords.isEmpty()) {
            spec = spec.and(CourseSpecifications.containsKeywords(keywords));
        }

        if (date != null) {
            spec = spec.and(CourseSpecifications.hasVisitDate(date));
        }

        if (with != null && !with.isBlank()) {
            spec = spec.and(CourseSpecifications.visitedWith(with));
        }

        if (region != null && !region.isBlank()) {
            spec = spec.and(CourseSpecifications.inRegion(region));
        }

        // TODO: 업종 코드 추가

        if (tags != null && !tags.isEmpty()) {
            spec = spec.and(CourseSpecifications.hasTagIn(tags));
        }

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return courseRepository.findAll(spec, sortedPageable);
    }
}
