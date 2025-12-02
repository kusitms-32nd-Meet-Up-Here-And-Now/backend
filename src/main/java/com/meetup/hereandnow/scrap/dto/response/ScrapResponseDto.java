package com.meetup.hereandnow.scrap.dto.response;

import com.meetup.hereandnow.scrap.domain.CourseScrap;
import com.meetup.hereandnow.scrap.domain.PlaceScrap;
import io.swagger.v3.oas.annotations.media.Schema;

public record ScrapResponseDto(

        @Schema(description = "스크랩 ID", example = "3")
        Long scrapId,

        @Schema(description = "사용자 ID", example = "2")
        Long memberId,

        @Schema(description = "스크랩 대상 ID", example = "1")
        Long targetId,

        @Schema(description = "스크랩 취소인지 여부", example = "false")
        boolean deleted
) {

    public static ScrapResponseDto from(PlaceScrap placeScrap) {
        return new ScrapResponseDto(
                placeScrap.getId(),
                placeScrap.getMember().getId(),
                placeScrap.getPlace().getId(),
                false
        );
    }

    public static ScrapResponseDto from(CourseScrap courseScrap) {
        return new ScrapResponseDto(
                courseScrap.getId(),
                courseScrap.getMember().getId(),
                courseScrap.getCourse().getId(),
                false
        );
    }

    public static ScrapResponseDto from() {
        return new ScrapResponseDto(null, null, null, true);
    }
}
