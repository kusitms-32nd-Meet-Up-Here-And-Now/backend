package com.meetup.hereandnow.auth.application;

import com.meetup.hereandnow.auth.application.jwt.AccessTokenService;
import com.meetup.hereandnow.auth.application.jwt.RefreshTokenService;
import com.meetup.hereandnow.auth.dto.response.LogoutResponse;
import com.meetup.hereandnow.auth.dto.response.TokenResponse;
import com.meetup.hereandnow.auth.exception.JwtErrorCode;
import com.meetup.hereandnow.auth.exception.OAuth2ErrorCode;
import com.meetup.hereandnow.auth.infrastructure.jwt.JwtProperties;
import com.meetup.hereandnow.auth.infrastructure.jwt.TokenProvider;
import com.meetup.hereandnow.core.util.SecurityUtils;
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

    /**
     * 로그인 완료 이후 발급된 code를 통해 AccessToken을 발급하는 method
     * @param authKey redirect url에 붙은 query param
     * @return accessToken, refreshToken
     */
    public TokenResponse getAccessTokenByAuthKey(String authKey) {
        String accessToken = accessTokenService.getToken(authKey);

        if(accessToken == null) {
            throw OAuth2ErrorCode.NOT_FOUND_AUTH_INFO.toException();
        }

        Claims claims = tokenProvider.resolveTokenClaims(accessToken);

        if(!claims.get("type").equals("Access")) {
            accessTokenService.deleteToken(authKey);
            throw JwtErrorCode.TOKEN_INVALID.toException();
        }

        String refreshToken = refreshTokenService.getToken(Long.valueOf(claims.getSubject()));

        return new TokenResponse(accessToken, refreshToken);
    }

    /**
     * 로그아웃 메서드
     * @return 로그아웃 성공 메시지
     */
    public LogoutResponse logout() {

        Long memberId = getCurrentMember().getId();

        String refreshToken = refreshTokenService.getToken(memberId);
        if (refreshToken == null) {
            throw JwtErrorCode.REFRESH_TOKEN_NOT_FOUND.toException();
        }

        refreshTokenService.deleteToken(memberId);

        return new LogoutResponse(true, "성공적으로 로그아웃 되었습니다.");
    }

    /**
     * accessToken 재발행 메서드
     * @param refreshToken 토큰 재 발행을 위한 accessToken
     * @return 새로 발급된 accessToken, refreshToken
     */
    public TokenResponse reissue(String refreshToken) {

        Member member = getCurrentMember();
        if(member == null) {
            throw MemberErrorCode.MEMBER_NOT_FOUND.toException();
        }

        String storedRefreshToken = refreshTokenService.getToken(member.getId());
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw JwtErrorCode.REFRESH_TOKEN_NOT_FOUND.toException();
        }

        String newAccessToken = tokenProvider.createAccessToken(member);
        String newRefreshToken = tokenProvider.createRefreshToken();

        refreshTokenService.saveToken(member.getId(), newRefreshToken, Duration.ofSeconds(jwtProperties.refreshExp()));

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    /**
     * 현재 로그인된 멤버 불러오는 유틸 함수
     * 무분별한 @AuthenticationPrincipal 사용 방지를 위함
     * @return 현재 로그인된 멤버
     */
    public Member getCurrentMember() {
        return SecurityUtils.getCurrentMember();
    }
}
