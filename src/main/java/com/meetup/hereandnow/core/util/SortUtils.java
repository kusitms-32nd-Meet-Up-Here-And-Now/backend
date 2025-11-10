package com.meetup.hereandnow.core.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;

/**
 * 전달된 정렬 값을 pageable로 알맞게 처리합니다. 기본값은 최신순입니다.
 */
public class SortUtils {

    private SortUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static Pageable resolveCourseSort(int page, int size, String sort) {
        String sortBy = Optional.ofNullable(sort).orElse("");
        String resolvedSortBy;
        if (sortBy.equalsIgnoreCase("scraps")) {
            resolvedSortBy = "course.scrapCount";
        } else {
            resolvedSortBy = "createdAt";
        }
        // TODO: 추후 댓글 수 (리뷰 많은 순) 역순 추가
        return PageRequest.of(
                page, size,
                Sort.by(Sort.Direction.DESC, resolvedSortBy)
        );
    }

    public static Pageable resolvePlaceSort(int page, int size, String sort) {
        String sortBy = Optional.ofNullable(sort).orElse("");
        String resolvedSortBy = switch (sortBy.toLowerCase()) {
            case "scraps" -> "place.scrapCount";
            case "reviews" -> "place.pinCount";
            default -> "createdAt";
        };
        return PageRequest.of(
                page, size,
                Sort.by(Sort.Direction.DESC, resolvedSortBy)
        );
    }
}
