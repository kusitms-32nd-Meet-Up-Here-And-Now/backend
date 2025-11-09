package com.meetup.hereandnow.connect.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CoupleConnectingRequestDto(
        @Schema(description = "상대방 username", example = "hereandnow")
        String opponentUsername
) {
}
