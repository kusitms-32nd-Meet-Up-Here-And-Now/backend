package com.meetup.hereandnow.pin.dto;

import com.meetup.hereandnow.place.dto.PlaceSaveDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PinSaveDto(

        @Schema(description = "핀 제목", example = "홍대 느좋 카페")
        String pinTitle,

        @Schema(description = "핀 평점", example = "4.5")
        double pinRating,

        @Schema(description = "핀 설명", example = "커피가 맛있고 분위기가 좋아요")
        String pinDescription,

        @Schema(description = "장소의 분류 코드", example = "CT1")
        String placeGroupCode,

        @Schema(description = "태그의 상세 이름 값", example = "[\"야경이 예뻐요\", \"주차하기 편해요\"]")
        List<String> pinTagNames,

        CouplePinSaveRequestDto couplePinSaveRequestDto,

        PlaceSaveDto place
) {
}
