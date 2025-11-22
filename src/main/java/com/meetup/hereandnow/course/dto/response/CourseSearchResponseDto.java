package com.meetup.hereandnow.course.dto.response;

import com.meetup.hereandnow.archive.dto.response.CourseFolderResponseDto;

import java.util.List;

public record CourseSearchResponseDto(

        SearchFilterDto selectedFilters,

        List<CourseFolderResponseDto> filteredCourses
) {
}
