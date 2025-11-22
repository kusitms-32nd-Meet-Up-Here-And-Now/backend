package com.meetup.hereandnow.place.dto.request;

public record PlaceRatingDto(
        Long placeId,
        Double placeRating,
        Long pinCount
) {
}
