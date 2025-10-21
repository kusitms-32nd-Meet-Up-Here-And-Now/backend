package com.meetup.hereandnow.pin.dto;

import com.meetup.hereandnow.pin.domain.value.PinTagEnum;
import com.meetup.hereandnow.place.dto.PlaceSaveDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record PinSaveDto(

        @Schema(description = "핀 제목", example = "연남동 느좋 카페")
        String pinTitle,

        @Schema(description = "핀 평점", example = "4.5")
        double pinRating,

        @Schema(description = "핀 설명", example = "커피가 맛있고 분위기가 좋아요")
        String pinDescription,

        @Schema(description = "핀 태그", example = "COZY, EXCITED")
        List<PinTagEnum> pinTags,

        PlaceSaveDto place
) {
}
