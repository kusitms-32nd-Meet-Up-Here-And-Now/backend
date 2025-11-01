package com.meetup.hereandnow.place.dto;

public record PlaceTagDto(
        Long placeId,
        String tagName,
        Long tagCount
) {
}
