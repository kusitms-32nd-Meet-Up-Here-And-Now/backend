package com.meetup.hereandnow.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceDetailsResponseDto(

        @Schema(description = "장소명", example = "서울숲")
        String placeName,

        @Schema(description = "도로명주소", example = "서울 성동구 성수동1가 678-1")
        String placeStreetNameAddress,

        @Schema(description = "위도", example = "37.5446313")
        Double placeLatitude,

        @Schema(description = "경도", example = "127.0374023")
        Double placeLongitude,

        @Schema(description = "저장(좋아요) 여부", example = "false")
        Boolean scrapped,

        @Schema(description = "장소 평점", example = "4.7")
        Double placeRating,

        @Schema(description = "장소 리뷰 수", example = "531")
        Long reviewCount

        // TODO: 장소 카카오맵 고유 id 추가 (리다이렉트용)
) {
}
