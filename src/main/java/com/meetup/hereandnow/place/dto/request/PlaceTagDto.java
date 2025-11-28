package com.meetup.hereandnow.place.dto.request;

public record PlaceTagDto(
        Long placeId,
        String tagName,
        Long tagCount
) {
}
