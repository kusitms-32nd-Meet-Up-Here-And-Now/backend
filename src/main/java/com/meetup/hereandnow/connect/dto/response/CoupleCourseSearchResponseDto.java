package com.meetup.hereandnow.connect.dto.response;

import java.util.List;

public record CoupleCourseSearchResponseDto(
        CoupleCourseSearchFilterDto selectedFilters,

        List<CoupleCourseFolderResponseDto> filteredCourses
) {
}
