package com.meetup.hereandnow.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record TokenIssueRequest(

        @Schema(description = "로그인 이후 발급되는 쿼리파라미터 값 (?code=...)", example = "0o98vd-...")
        String authKey
) {
}
