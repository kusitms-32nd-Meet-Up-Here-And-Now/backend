package com.meetup.hereandnow.place.dto.response;

import com.meetup.hereandnow.place.domain.Place;
import io.swagger.v3.oas.annotations.media.Schema;
import org.locationtech.jts.geom.Point;

public record PlaceMarkerResponseDto(

        @Schema(description = "장소 위도", example = "37.xxxxx")
        double latitude,

        @Schema(description = "장소 경도", example = "127.xxxxx")
        double longitude
) {
    public static PlaceMarkerResponseDto from(Place place) {
        Point location = place.getLocation();
        double latitude = (location != null) ? location.getY() : 0.0;
        double longitude = (location != null) ? location.getX() : 0.0;
        return new PlaceMarkerResponseDto(
                latitude,
                longitude
        );
    }
}
