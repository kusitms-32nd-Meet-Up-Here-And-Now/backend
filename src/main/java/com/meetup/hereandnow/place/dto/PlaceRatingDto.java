package com.meetup.hereandnow.place.dto;

public record PlaceRatingDto(
        Long placeId,
        Double placeRating,
        Long pinCount
) {
}
