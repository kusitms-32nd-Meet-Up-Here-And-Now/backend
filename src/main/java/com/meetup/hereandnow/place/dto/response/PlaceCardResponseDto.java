package com.meetup.hereandnow.place.dto.response;

import com.meetup.hereandnow.place.domain.Place;
import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceCardResponseDto(

        @Schema(description = "장소 ID", example = "1")
        Long placeId,

        @Schema(description = "장소 이름", example = "갓덴스시 강남점")
        String placeName,

        @Schema(description = "장소 카테고리", example = "음식점")
        String placeCategory,

        @Schema(description = "장소 도로명주소", example = "서울 강남구 강남대로102길 30 1-3층")
        String placeStreetNameAddress,

        @Schema(description = "장소 지번주소", example = "역삼동 822-4")
        String placeNumberAddress,

        @Schema(description = "장소 평점", example = "4.1")
        double placeRating,

        @Schema(description = "장소 리뷰 수", example = "252")
        long reviewCount,

        @Schema(description = "장소 이미지", example = "https://...")
        String placeImageUrl,

        @Schema(description = "장소 위도", example = "37.2384738")
        double lat,

        @Schema(description = "장소 경도", example = "127.239428")
        double lon
) {
    public static PlaceCardResponseDto from(Place place, String imageUrl) {
        return new PlaceCardResponseDto(
                place.getId(),
                place.getPlaceName(),
                place.getPlaceCategory(),
                place.getPlaceStreetNameAddress(),
                place.getPlaceNumberAddress(),
                place.getPlaceRating().doubleValue(),
                place.getPinCount(),
                imageUrl,
                place.getLocation().getX(),
                place.getLocation().getY()
        );
    }
}
