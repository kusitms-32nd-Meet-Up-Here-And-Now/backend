package com.meetup.hereandnow.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceDetailsResponseDto(

        @Schema(description = "장소명", example = "서울숲")
        String placeName,

        @Schema(description = "장소 카테고리", example = "도시근린공원")
        String placeCategory,

        @Schema(description = "도로명주소", example = "서울 성동구 성수동1가 678-1")
        String placeStreetNameAddress,

        @Schema(description = "위도", example = "37.5446313")
        Double placeLatitude,

        @Schema(description = "경도", example = "127.0374023")
        Double placeLongitude,

        @Schema(description = "장소 평점", example = "4.7")
        Double placeRating,

        @Schema(description = "장소 리뷰 수", example = "531")
        Long reviewCount,

        @Schema(description = "장소 카카오맵 url", example = "http://place.map.kakao.com/16618597")
        String placeUrl,

        @Schema(description = "저장(좋아요) 여부", example = "false")
        Boolean scrapped
) {
}
