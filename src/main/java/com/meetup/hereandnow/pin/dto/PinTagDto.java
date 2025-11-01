package com.meetup.hereandnow.pin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PinTagDto(

        @Schema(description = "장소의 분류 코드", example = "CT1")
        String placeGroupCode,

        @Schema(description = "태그의 상세 이름 값", example = "야경이 예뻐요")
        String name
) {
}
