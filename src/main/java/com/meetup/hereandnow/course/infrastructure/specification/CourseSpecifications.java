package com.meetup.hereandnow.course.infrastructure.specification;

import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.member.domain.Member;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CourseSpecifications {

    private static final char ESCAPE_CHAR = '\\';

    private static String escapeLike(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replace(String.valueOf(ESCAPE_CHAR), ESCAPE_CHAR + String.valueOf(ESCAPE_CHAR))
                .replace("%", ESCAPE_CHAR + "%")
                .replace("_", ESCAPE_CHAR + "_");
    }

    // 코스 작성자
    public static Specification<Course> hasMember(Member member) {
        return (root, query, cb) ->
                cb.equal(root.get("member"), member);
    }

    // 평점
    public static Specification<Course> isRatingInRange(int rating) {
        return (root, query, cb) -> {

            // if 3점이면 3.0 이상 4.0 미만 조회되도록
            BigDecimal lowerBound = BigDecimal.valueOf(rating);
            BigDecimal upperBound = BigDecimal.valueOf(rating + 1);

            Predicate gte = cb.greaterThanOrEqualTo(root.get("courseRating"), lowerBound);
            Predicate lt = cb.lessThan(root.get("courseRating"), upperBound);

            return cb.and(gte, lt);
        };
    }

    // 키워드
    public static Specification<Course> containsKeywords(List<String> keywords) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (String keyword : keywords) {
                String escapedKeyword = escapeLike(keyword);
                Predicate titleLike = cb.like(root.get("courseTitle"), "%" + escapedKeyword + "%", ESCAPE_CHAR);
                Predicate descriptionLike = cb.like(root.get("courseDescription"), "%" + escapedKeyword + "%", ESCAPE_CHAR);
                predicates.add(cb.or(titleLike, descriptionLike));
            }
            return cb.or(predicates.toArray(new Predicate[0]));
        };
    }

    // 날짜
    public static Specification<Course> isVisitDateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("courseVisitDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("courseVisitDate"), endDate));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
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

    // 업종 코드
    public static Specification<Course> hasPlaceGroupCodeIn(List<String> placeCode) {
        return (root, query, cb) -> {
            query.distinct(true);
            return root.join("pinList")
                    .join("place")
                    .join("placeGroup")
                    .get("code")
                    .in(placeCode);
        };
    }

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

    // 공개 코스인 것만
    public static Specification<Course> isPublic() {
        return (root, query, cb) ->
                cb.isTrue(root.get("isPublic"));
    }
}
