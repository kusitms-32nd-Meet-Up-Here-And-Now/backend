package com.meetup.hereandnow.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReIssueTokenRequest(

        @Schema(description = "Refresh Token", example = "refresh.token")
        String refreshToken
) {
}
