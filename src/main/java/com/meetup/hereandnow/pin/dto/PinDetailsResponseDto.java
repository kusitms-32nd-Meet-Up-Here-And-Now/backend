package com.meetup.hereandnow.pin.dto;

import com.meetup.hereandnow.place.dto.response.PlaceDetailsResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PinDetailsResponseDto(

        @Schema(description = "코스 내 장소 인덱스", example = "1")
        Integer pinIndex,

        PlaceDetailsResponseDto placeDetails,

        @Schema(description = "등록한 이미지 리스트", example = "[\"https://kr.../a558.jpg\"]")
        List<String> pinImages,

        @Schema(description = "좋았던 점", example = "좋았던 점")
        String pinPositive,

        @Schema(description = "아쉬웠던 점", example = "아쉬웠던 점")
        String pinNegative
) {
}
