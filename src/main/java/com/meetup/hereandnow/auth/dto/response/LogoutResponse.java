package com.meetup.hereandnow.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record LogoutResponse(
    @Schema(description = "로그아웃 성공 여부", example = "true")
    Boolean isSuccess,

    @Schema(description = "메시지", example = "로그아웃에 성공했습니다.")
    String message
) {

}
