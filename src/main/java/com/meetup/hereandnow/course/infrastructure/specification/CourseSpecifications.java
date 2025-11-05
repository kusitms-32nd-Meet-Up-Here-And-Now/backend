package com.meetup.hereandnow.course.infrastructure.specification;

import com.meetup.hereandnow.course.domain.entity.Course;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CourseSpecifications {

    // 평점
    public static Specification<Course> hasRatingGreaterThanOrEqual(int rating) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("courseRating"), BigDecimal.valueOf(rating));
    }

    // 키워드
    public static Specification<Course> containsKeywords(List<String> keywords) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (String keyword : keywords) {
                // 제목, 설명 비교
                Predicate titleLike = cb.like(root.get("courseTitle"), "%" + keyword + "%");
                Predicate descriptionLike = cb.like(root.get("courseDescription"), "%" + keyword + "%");
                predicates.add(cb.or(titleLike, descriptionLike));
            }
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    // 날짜
    public static Specification<Course> hasVisitDate(LocalDate date) {
        return (root, query, cb) ->
                cb.equal(root.get("courseVisitDate"), date);
    }

    // 누구와 방문
    public static Specification<Course> visitedWith(String with) {
        return (root, query, cb) ->
                cb.like(root.get("courseVisitMember"), "%" + with + "%");
    }

    // 지역
    public static Specification<Course> inRegion(String region) {
        return (root, query, cb) ->
                cb.like(root.get("courseRegion"), "%" + region + "%");
    }

    // TODO: 업종 코드 필터링 추가 - placeGroupCode 리팩토링 이후

    // 태그
    public static Specification<Course> hasTagIn(List<String> tags) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (String tag : tags) {
                predicates.add(cb.like(root.get("courseTags"), "%" + tag + "%"));
            }
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }
}
