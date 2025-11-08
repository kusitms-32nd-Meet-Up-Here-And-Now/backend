package com.meetup.hereandnow.connect.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

public record CoupleInfoRequestDto(
        @Schema(description = "커플 시작 날짜", example = "2025.11.08")
        @JsonFormat(pattern = "yyyy.MM.dd")
        LocalDate coupleStartDate,

        @Schema(description = "커플 메인 배너 이미지 objectkey", example = "/couple/{coupleId}/banner/029300.png")
        String imageObjectKey
) {
}
