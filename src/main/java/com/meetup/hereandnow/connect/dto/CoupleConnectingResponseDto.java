package com.meetup.hereandnow.connect.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CoupleConnectingResponseDto(
        @Schema(description = "현재 생성된 커플 식별자", example = "1")
        Long id,

        @Schema(description = "나의 username", example = "hereandnow_1")
        String myUsername,

        @Schema(description = "상대방 username", example = "hereandnow_2")
        String opponentUsername,

        @Schema(description = "커플 생성 여부 메시지", example = "커플 생성에 성공했습니다.")
        String message
) {

    public static CoupleConnectingResponseDto from(
            Long id,
            String myUsername,
            String opponentUsername
    ) {
        return new CoupleConnectingResponseDto(
                id, myUsername, opponentUsername, "커플 생성에 성공했습니다."
        );
    }
}
