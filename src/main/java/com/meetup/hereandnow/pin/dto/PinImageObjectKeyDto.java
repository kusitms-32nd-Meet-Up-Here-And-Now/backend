package com.meetup.hereandnow.pin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record PinImageObjectKeyDto(
        @Schema(description = "핀 인덱스", example = "1")
        int pinIdx,

        @Schema(
                description = "핀 이미지 ObjectKey 리스트 (순서대로)",
                example = "[\"/course/{courseUUID}/pins/{pinUUID}/images/01de32c9.jpg\"]"
        )
        List<String> objectKeyList
) {
}
