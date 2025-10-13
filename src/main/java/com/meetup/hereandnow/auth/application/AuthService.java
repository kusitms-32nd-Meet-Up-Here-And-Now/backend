package com.meetup.hereandnow.auth.application;

import com.meetup.hereandnow.auth.application.jwt.AccessTokenService;
import com.meetup.hereandnow.auth.application.jwt.RefreshTokenService;
import com.meetup.hereandnow.auth.dto.response.LogoutResponse;
import com.meetup.hereandnow.auth.dto.response.TokenResponse;
import com.meetup.hereandnow.auth.exception.JwtErrorCode;
import com.meetup.hereandnow.auth.exception.OAuth2ErrorCode;
import com.meetup.hereandnow.auth.infrastructure.jwt.JwtProperties;
import com.meetup.hereandnow.auth.infrastructure.jwt.TokenProvider;
import com.meetup.hereandnow.core.infrastructure.security.CustomUserDetails;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.exception.MemberErrorCode;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {

    private final AccessTokenService accessTokenService;
    private final RefreshTokenService refreshTokenService;
    private final TokenProvider tokenProvider;
    private final JwtProperties jwtProperties;

    public TokenResponse getAccessTokenByAuthKey(String authKey) {
        String accessToken = accessTokenService.getToken(authKey);

        if(accessToken == null) {
            throw OAuth2ErrorCode.NOT_FOUND_AUTH_INFO.toException();
        }

        Claims claims = tokenProvider.resolveTokenClaims(accessToken);

        if(claims.get("type").equals("Refresh")) {
            throw JwtErrorCode.TOKEN_INVALID.toException();
        }

        String refreshToken = refreshTokenService.getToken(Long.valueOf(claims.getSubject()));

        return new TokenResponse(accessToken, refreshToken);
    }

    public LogoutResponse logout(CustomUserDetails customUserDetails) {
        Long memberId = customUserDetails.member().getId();
        String refreshToken = refreshTokenService.getToken(memberId);
        if (refreshToken == null) {
            throw JwtErrorCode.REFRESH_TOKEN_NOT_FOUND.toException();
        }
        refreshTokenService.deleteToken(memberId);
        return new LogoutResponse(true, "성공적으로 로그아웃 되었습니다.");
    }

    public TokenResponse reissue(CustomUserDetails customUserDetails, String refreshToken) {

        if(customUserDetails == null) {
            throw MemberErrorCode.MEMBER_NOT_FOUND.toException();
        }

        Member member = customUserDetails.member();

        String storedRefreshToken = refreshTokenService.getToken(member.getId());
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw JwtErrorCode.REFRESH_TOKEN_NOT_FOUND.toException();
        }

        String newAccessToken = tokenProvider.createAccessToken(member);
        String newRefreshToken = tokenProvider.createRefreshToken();

        refreshTokenService.saveToken(member.getId(), newRefreshToken, Duration.ofSeconds(jwtProperties.refreshExp()));

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}
