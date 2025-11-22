package com.meetup.hereandnow.place.dto.response;

import com.meetup.hereandnow.place.domain.Place;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PlacePointResponseDto(

        @Schema(description = "장소 id", example = "1")
        Long placeId,

        @Schema(description = "장소명", example = "갓덴스시 강남점")
        String placeName,

        PlaceMarkerResponseDto placeMarker,

        @Schema(description = "장소 사진 리스트", example = "[\"https://...\", \"https://...\"]")
        List<String> imageUrl
) {
    public static PlacePointResponseDto from(Place place, List<String> imageUrl) {
        return new PlacePointResponseDto(
                place.getId(),
                place.getPlaceName(),
                PlaceMarkerResponseDto.from(place),
                imageUrl
        );
    }
}
