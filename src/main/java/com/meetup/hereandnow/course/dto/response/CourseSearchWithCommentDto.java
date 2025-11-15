package com.meetup.hereandnow.course.dto.response;

import java.util.List;

public record CourseSearchWithCommentDto(

        SearchFilterDto selectedFilters,

        List<CourseCardWithCommentDto> filteredCourses
) {
}
