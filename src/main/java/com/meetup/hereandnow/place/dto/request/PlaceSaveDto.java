package com.meetup.hereandnow.place.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record PlaceSaveDto(

        @Schema(description = "장소 이름", example = "스타벅스 홍대입구역점")
        String placeName,

        @Schema(description = "장소 도로명 주소", example = "서울 마포구 양화로 165 상진빌딩 1층 (동교동)")
        String placeStreetNameAddress,

        @Schema(description = "장소 지번 주소", example = "서울시 마포구 동교동 159-1 상진빌딩 1층")
        String placeNumberAddress,

        @Schema(description = "장소 위도", example = "37.55724635884168")
        double placeLatitude,

        @Schema(description = "장소 경도", example = "126.92367663863469")
        double placeLongitude,

        @Schema(description = "장소의 분류 코드", example = "FD6")
        String placeGroupCode,

        @Schema(description = "장소의 세부 카테고리", example = "음식점/카페 > 카페")
        String placeCategory,

        @Schema(description = "장소 바로가기 URL", example = "https://place.map.kakao.com/22105109")
        String placeUrl
) {
}
