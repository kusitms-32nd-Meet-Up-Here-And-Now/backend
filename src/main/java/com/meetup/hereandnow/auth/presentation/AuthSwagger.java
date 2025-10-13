package com.meetup.hereandnow.auth.presentation;

import com.meetup.hereandnow.auth.dto.request.ReIssueTokenRequest;
import com.meetup.hereandnow.auth.dto.request.TokenIssueRequest;
import com.meetup.hereandnow.auth.dto.response.LogoutResponse;
import com.meetup.hereandnow.auth.dto.response.TokenResponse;
import com.meetup.hereandnow.auth.exception.JwtErrorCode;
import com.meetup.hereandnow.auth.exception.OAuth2ErrorCode;
import com.meetup.hereandnow.core.config.swagger.ApiErrorCode;
import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.member.exception.MemberErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Auth", description = "로그인 및 토큰 관련 API")
public interface AuthSwagger {

    @Operation(
            summary = "OAuth2 로그인 완료시 AccessToken 받아오는 API",
            description = "OAuth2 로그인 완료 이후 발행되는 code Param을 활용하여 AccessToken을 받아옵니다.<br>" +
                    "프론트엔드 redirect 주소로 .../?code=... 이렇게 전달되는 code를 전달하여 받아옵니다.<br>" +
                    "전달 이후 검증 과정을 통해 레디스에 저장된 accessToken을 받아옵니다.",
            operationId = "/auth/token"
    )
    @ApiErrorCode({OAuth2ErrorCode.class, JwtErrorCode.class})
    ResponseEntity<RestResponse<TokenResponse>> issueToken(
            @RequestBody TokenIssueRequest request
    );

    @Operation(
            summary = "로그아웃 API",
            description = "로그아웃을 진행합니다. 저장된 refreshToken을 삭제합니다.<br>" +
                    "accessToken 정보는 프론트엔드에서 삭제합니다.",
            operationId = "/auth/logout"
    )
    @ApiErrorCode({JwtErrorCode.class})
    ResponseEntity<RestResponse<LogoutResponse>> logout();

    @Operation(
            summary = "토큰 재발행 API",
            description = "refreshToken을 통해 토큰을 재발행합니다.",
            operationId = "/auth/re-issue"
    )
    @ApiErrorCode({MemberErrorCode.class, JwtErrorCode.class})
    ResponseEntity<RestResponse<TokenResponse>> reissueToken(
            @RequestBody ReIssueTokenRequest request
    );
}
