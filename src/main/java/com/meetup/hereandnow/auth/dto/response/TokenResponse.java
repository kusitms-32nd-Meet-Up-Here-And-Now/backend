package com.meetup.hereandnow.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenResponse(

        @Schema(description = "accessToken", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "refreshToken", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String refreshToken
) {
}
