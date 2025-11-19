package com.meetup.hereandnow.connect.domain.vo;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class CourseSearchCriteria {
    private final Integer rating;
    private final List<String> keywords;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String region;
    private final List<String> placeCode;
    private final List<String> tags;
}

