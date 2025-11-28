package com.meetup.hereandnow.place.dto.response;

import com.meetup.hereandnow.course.dto.response.SearchFilterDto;

import java.util.List;

public record PlaceSearchResponseDto(

        SearchFilterDto selectedFilters,

        List<PlaceCardMarkerResponseDto> filteredPlaces
) {
}
