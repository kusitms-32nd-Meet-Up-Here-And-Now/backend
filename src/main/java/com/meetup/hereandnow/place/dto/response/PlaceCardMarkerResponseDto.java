package com.meetup.hereandnow.place.dto.response;

public record PlaceCardMarkerResponseDto(

        PlaceCardResponseDto placeCard,

        PlaceMarkerResponseDto placeMarker
) {
    public static PlaceCardMarkerResponseDto of(PlaceCardResponseDto placeCard, PlaceMarkerResponseDto placeMarker) {
        return new PlaceCardMarkerResponseDto(placeCard, placeMarker);
    }
}
