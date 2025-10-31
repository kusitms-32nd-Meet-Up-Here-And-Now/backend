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

        @Schema(description = "장소 평점", example = "4.5")
        double placeRating,

        @Schema(description = "장소 태그", example = "[\"뷰가 좋아요\",\"사진이 잘 나와요\"]")
        List<String> placeTag,

        @Schema(description = "장소 사진", example = "[\"course/8b18/pins/92eb/images/a558.jpg\",\"course/8b18/pins/92eb/images/a5eh.jpg\"]")
        List<String> imageUrl
) {
}
