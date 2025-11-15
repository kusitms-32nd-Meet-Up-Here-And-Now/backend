package com.meetup.hereandnow.place.infrastructure.specification;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.pin.domain.entity.Pin;
import com.meetup.hereandnow.place.domain.Place;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PlaceSpecifications {

    private static final char ESCAPE_CHAR = '\\';

    private static String escapeLike(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replace(String.valueOf(ESCAPE_CHAR), ESCAPE_CHAR + String.valueOf(ESCAPE_CHAR))
                .replace("%", ESCAPE_CHAR + "%")
                .replace("_", ESCAPE_CHAR + "_");
    }

    // 평점
    public static Specification<Place> isRatingInRange(Integer rating) {
        return (root, query, cb) -> {
            BigDecimal lowerBound = BigDecimal.valueOf(rating);
            BigDecimal upperBound = BigDecimal.valueOf(rating + 1);
            Predicate gte = cb.greaterThanOrEqualTo(root.get("placeRating"), lowerBound);
            Predicate lt = cb.lessThan(root.get("placeRating"), upperBound);
            return cb.and(gte, lt);
        };
    }

    // 키워드
    public static Specification<Place> containsKeywords(List<String> keywords) {
        return (root, query, cb) -> {
            List<Predicate> keywordOrPredicates = new ArrayList<>();
            for (String keyword : keywords) {
                String escapedKeyword = escapeLike(keyword);
                // 장소명, 카테고리 검색
                Predicate p1 = cb.like(root.get("placeName"), "%" + escapedKeyword + "%", ESCAPE_CHAR);
                Predicate p2 = cb.like(root.get("placeCategory"), "%" + escapedKeyword + "%", ESCAPE_CHAR);
                keywordOrPredicates.add(cb.or(p1, p2));
            }
            return cb.or(keywordOrPredicates.toArray(new Predicate[0]));
        };
    }

    // 날짜 범위 내에 저장된 코스에 포함된 장소인지
    public static Specification<Place> isPinnedByCourseInDateRange(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Course> courseRoot = subquery.from(Course.class);
            Join<Course, Pin> pinJoin = courseRoot.join("pinList");

            subquery.select(cb.literal(1L));

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(pinJoin.get("place"), root));

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(courseRoot.get("courseVisitDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(courseRoot.get("courseVisitDate"), endDate));
            }

            subquery.where(cb.and(predicates.toArray(new Predicate[0])));
            return cb.exists(subquery);
        };
    }

    // '누구'와 함께했다고 표시된 코스에 포함된 장소인지
    public static Specification<Place> isPinnedByCourseWith(String with) {
        return (root, query, cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Course> courseRoot = subquery.from(Course.class);
            Join<Course, Pin> pinJoin = courseRoot.join("pinList");

            subquery.select(cb.literal(1L));
            subquery.where(cb.and(
                    cb.equal(pinJoin.get("place"), root),
                    cb.like(courseRoot.get("courseVisitMember"), "%" + with + "%")
            ));
            return cb.exists(subquery);
        };
    }

    // 코스 지역
    public static Specification<Place> isPinnedByCourseInRegion(String region) {
        return (root, query, cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Course> courseRoot = subquery.from(Course.class);
            Join<Course, Pin> pinJoin = courseRoot.join("pinList");

            subquery.select(cb.literal(1L));
            subquery.where(cb.and(
                    cb.equal(pinJoin.get("place"), root),
                    cb.like(courseRoot.get("courseRegion"), "%" + region + "%")
            ));
            return cb.exists(subquery);
        };
    }

    // 장소 업종 코드
    public static Specification<Place> hasPlaceGroupCodeIn(List<String> placeCodes) {
        return (root, query, cb) ->
                root.join("placeGroup").get("code").in(placeCodes);
    }

    // 장소 태그
    public static Specification<Place> hasTagIn(List<String> tags) {
        return (root, query, cb) -> {
            List<Predicate> tagPredicates = new ArrayList<>();
            for (String tag : tags) {
                tagPredicates.add(cb.like(root.get("placeTags"), "%" + tag + "%"));
            }
            return cb.or(tagPredicates.toArray(new Predicate[0]));
        };
    }
}