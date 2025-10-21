package com.meetup.hereandnow.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceSaveDto(

        @Schema(description = "장소 이름", example = "스타벅스 홍대입구역점")
        String placeName,

        @Schema(description = "장소 주소", example = "서울특별시 마포구 동교동")
        String placeAddress,

        @Schema(description = "장소 위도", example = "37.77932")
        double placeLatitude,

        @Schema(description = "장소 경도", example = "127.74581")
        double placeLongitude
) {
}
