package com.meetup.hereandnow.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReIssueTokenRequest(

        @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String refreshToken
) {
}
