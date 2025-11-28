package com.meetup.hereandnow.connect.infrastructure.builder;

import com.meetup.hereandnow.connect.domain.vo.CourseSearchCriteria;
import com.meetup.hereandnow.connect.domain.vo.CourseVisitType;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.infrastructure.specification.CourseSpecifications;
import com.meetup.hereandnow.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoupleSpecificationBuilder {

    public Specification<Course> build(Member member, CourseSearchCriteria criteria) {
        Specification<Course> spec = Specification.where(CourseSpecifications.hasMember(member))
                .and(CourseSpecifications.visitedWith(CourseVisitType.COUPLE.getValue()));

        if (criteria.getRating() != null && criteria.getRating() > 0) {
            spec = spec.and(CourseSpecifications.isRatingInRange(criteria.getRating()));
        }

        if (criteria.getKeywords() != null && !criteria.getKeywords().isEmpty()) {
            spec = spec.and(CourseSpecifications.containsKeywords(criteria.getKeywords()));
        }

        if (criteria.getStartDate() != null || criteria.getEndDate() != null) {
            spec = spec.and(CourseSpecifications.isVisitDateBetween(criteria.getStartDate(), criteria.getEndDate()));
        }

        if (criteria.getRegion() != null && !criteria.getRegion().isBlank()) {
            spec = spec.and(CourseSpecifications.inRegion(criteria.getRegion()));
        }

        if (criteria.getPlaceCode() != null && !criteria.getPlaceCode().isEmpty()) {
            spec = spec.and(CourseSpecifications.hasPlaceGroupCodeIn(criteria.getPlaceCode()));
        }

        if (criteria.getTags() != null && !criteria.getTags().isEmpty()) {
            spec = spec.and(CourseSpecifications.hasTagIn(criteria.getTags()));
        }

        return spec;
    }
}

