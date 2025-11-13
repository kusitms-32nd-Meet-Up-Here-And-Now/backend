package com.meetup.hereandnow.core.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;
import java.util.Optional;

/**
 * 전달된 정렬 값을 pageable로 알맞게 처리합니다. 기본값은 최신순입니다.
 */
public class SortUtils {

    private SortUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static final String COURSE_DEFAULT_SORT = "createdAt";
    private static final Map<String, String> COURSE_SORT_MAP = Map.of(
            "scraps", "course.scrapCount"
    );

    private static final String COURSE_NATIVE_DEFAULT_SORT = "created_at";
    private static final Map<String, String> COURSE_NATIVE_SORT_MAP = Map.of(
            "scraps", "scrap_count"
    );

    private static final String PLACE_DEFAULT_SORT = "createdAt";
    private static final Map<String, String> PLACE_SORT_MAP = Map.of(
            "scraps", "place.scrapCount",
            "reviews", "place.pinCount"
    );

    private static final String PLACE_NATIVE_DEFAULT_SORT = "created_at";
    private static final Map<String, String> PLACE_NATIVE_SORT_MAP = Map.of(
            "scraps", "scrap_count",
            "reviews", "pin_count"
    );

    /**
     * Pageable 객체를 생성하는 로직
     */
    private static Pageable createPageable(int page, int size, String sortProperty) {
        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortProperty));
    }

    /**
     * sort 문자열을 실제 DB 필드명으로 변환하는 로직
     */
    private static String resolveSortProperty(
            String sort,
            Map<String, String> strategyMap,
            String defaultProperty
    ) {
        String sortBy = Optional.ofNullable(sort).orElse("").toLowerCase();
        return strategyMap.getOrDefault(sortBy, defaultProperty);
    }

    public static Pageable resolveCourseSort(int page, int size, String sort) {
        String resolvedSortBy = resolveSortProperty(sort, COURSE_SORT_MAP, COURSE_DEFAULT_SORT);
        return createPageable(page, size, resolvedSortBy);
    }

    public static Pageable resolveCourseSortNQ(int page, int size, String sort) {
        String resolvedSortBy = resolveSortProperty(sort, COURSE_NATIVE_SORT_MAP, COURSE_NATIVE_DEFAULT_SORT);
        return createPageable(page, size, resolvedSortBy);
    }

    public static Pageable resolvePlaceSort(int page, int size, String sort) {
        String resolvedSortBy = resolveSortProperty(sort, PLACE_SORT_MAP, PLACE_DEFAULT_SORT);
        return createPageable(page, size, resolvedSortBy);
    }

    public static Pageable resolvePlaceSortNQ(int page, int size, String sort) {
        String resolvedSortBy = resolveSortProperty(sort, PLACE_NATIVE_SORT_MAP, PLACE_NATIVE_DEFAULT_SORT);
        return createPageable(page, size, resolvedSortBy);
    }
}
