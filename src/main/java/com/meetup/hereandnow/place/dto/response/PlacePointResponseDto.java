package com.meetup.hereandnow.place.dto.response;

import com.meetup.hereandnow.place.domain.Place;
import io.swagger.v3.oas.annotations.media.Schema;
import org.locationtech.jts.geom.Point;

import java.util.List;

public record PlacePointResponseDto(

        @Schema(description = "장소 id", example = "1")
        Long placeId,

        @Schema(description = "장소명", example = "갓덴스시 강남점")
        String placeName,

        @Schema(description = "장소 위도", example = "37.xxxxx")
        double latitude,

        @Schema(description = "장소 경도", example = "127.xxxxx")
        double longitude,

        @Schema(description = "장소 사진 리스트", example = "[\"https://...\", \"https://...\"]")
        List<String> imageUrl
) {
    public static PlacePointResponseDto from(Place place, List<String> imageUrl) {
        Point location = place.getLocation();
        double latitude = (location != null) ? location.getY() : 0.0;
        double longitude = (location != null) ? location.getX() : 0.0;
        return new PlacePointResponseDto(
                place.getId(),
                place.getPlaceName(),
                latitude,
                longitude,
                imageUrl
        );
    }
}
