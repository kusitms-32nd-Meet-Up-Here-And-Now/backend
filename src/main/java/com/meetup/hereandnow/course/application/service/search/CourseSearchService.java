package com.meetup.hereandnow.course.application.service.search;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.course.infrastructure.specification.CourseSpecifications;
import com.meetup.hereandnow.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseSearchService {

    private final CourseRepository courseRepository;

    public Page<Course> searchCoursesByMember(
            Member member,
            Integer rating,
            List<String> keywords,
            LocalDate startDate,
            LocalDate endDate,
            String with,
            String region,
            List<String> placeCode,
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

        if (startDate != null || endDate != null) {
            spec = spec.and(CourseSpecifications.isVisitDateBetween(startDate, endDate));
        }

        if (with != null && !with.isBlank()) {
            spec = spec.and(CourseSpecifications.visitedWith(with));
        }

        if (region != null && !region.isBlank()) {
            spec = spec.and(CourseSpecifications.inRegion(region));
        }

        if (placeCode != null && !placeCode.isEmpty()) {
            spec = spec.and(CourseSpecifications.hasPlaceGroupCodeIn(placeCode));
        }

        if (tags != null && !tags.isEmpty()) {
            spec = spec.and(CourseSpecifications.hasTagIn(tags));
        }

        return courseRepository.findAll(spec, pageable);
    }
}
