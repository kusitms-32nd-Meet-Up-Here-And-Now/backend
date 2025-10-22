package com.meetup.hereandnow.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceSaveDto(

        @Schema(description = "장소 이름", example = "스타벅스 홍대입구역점")
        String placeName,

        @Schema(description = "장소 주소", example = "서울 마포구 양화로 165 상진빌딩 1층 (동교동)")
        String placeAddress,

        @Schema(description = "장소 위도", example = "37.55724635884168")
        double placeLatitude,

        @Schema(description = "장소 경도", example = "126.92367663863469")
        double placeLongitude
) {
}
