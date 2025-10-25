package com.meetup.hereandnow.pin.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PinDirnameDto(

        @Schema(name = "핀 순서(1부터 시작)", example = "1")
        int pinIdx,

        @Schema(name = "해당 핀에 대한 사진이 저장되어야하는 디렉토리 이름", example = "/course/{courseId}/pins/{pinId}/images")
        String pinDirname
) {
}
