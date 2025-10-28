package com.meetup.hereandnow.course.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CoupleCourseRecordSaveRequestDto(

        @Schema(description = "여자친구의 코스 설명", example = "정말 재밌게 놀았다.")
        String descriptionByGirlfriend,

        @Schema(description = "남자친구의 코스 설명", example = "너무 재밌었다.")
        String descriptionByBoyfriend
) {
}
