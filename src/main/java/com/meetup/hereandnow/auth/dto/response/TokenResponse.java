package com.meetup.hereandnow.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenResponse(

        @Schema(description = "accessToken", example = "accessToken")
        String accessToken,

        @Schema(description = "refreshToken", example = "refreshToken~")
        String refreshToken
) {
}
