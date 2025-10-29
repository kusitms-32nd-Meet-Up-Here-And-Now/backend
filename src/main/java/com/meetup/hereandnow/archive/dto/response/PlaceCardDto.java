package com.meetup.hereandnow.archive.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PlaceCardDto(

        @Schema(description = "장소 ID", example = "1")
        Long id,

        @Schema(description = "장소 이름", example = "스타벅스 홍대입구역점")
        String placeName,

        @Schema(description = "장소 주소", example = "서울 마포구 양화로 165 상진빌딩 1층 (동교동)")
        String placeAddress,

        @Schema(description = "장소 위도", example = "37.55724635884168")
        double placeLatitude,

        @Schema(description = "장소 경도", example = "126.92367663863469")
        double placeLongitude,

        @Schema(description = "장소 평점", example = "4")
        double placeRating,

        @Schema(description = "장소 사진", example = "[\"/course/8b18/pins/92eb/images/a558.jpg\",\"/course/8b18/pins/92eb/images/a5eh.jpg\"]")
        List<String> imageUrl
) {
}
