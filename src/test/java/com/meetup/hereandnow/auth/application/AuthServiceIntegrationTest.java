package com.meetup.hereandnow.auth.application;

import com.meetup.hereandnow.auth.application.jwt.AccessTokenService;
import com.meetup.hereandnow.auth.application.jwt.RefreshTokenService;
import com.meetup.hereandnow.auth.dto.response.LogoutResponse;
import com.meetup.hereandnow.auth.dto.response.TokenResponse;
import com.meetup.hereandnow.auth.exception.JwtErrorCode;
import com.meetup.hereandnow.auth.exception.OAuth2ErrorCode;
import com.meetup.hereandnow.auth.infrastructure.jwt.JwtProperties;
import com.meetup.hereandnow.auth.infrastructure.jwt.TokenProvider;
import com.meetup.hereandnow.core.exception.DomainException;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.integration.support.IntegrationTestSupport;
import com.meetup.hereandnow.member.domain.Member;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
class AuthServiceIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private AuthService authService;

    private final String AUTH_KEY = "test-auth-key";
    private final Long MEMBER_ID = 100L;
    private Member mockMember;
    private MockedStatic<SecurityUtils> mockedSecurity;

    @BeforeEach
    void setUp() {
        accessTokenService.deleteToken(AUTH_KEY);
        refreshTokenService.deleteToken(MEMBER_ID);

        mockedSecurity = Mockito.mockStatic(SecurityUtils.class);
        mockMember = Member.builder().id(MEMBER_ID).build();
        mockedSecurity.when(SecurityUtils::getCurrentMember).thenReturn(mockMember);
    }

    @AfterEach
    void tearDown() {
        mockedSecurity.close();
    }

    @Nested
    @DisplayName("토큰 발급 관련 테스트")
    class GetAccessTokenByAuthKey {

        @Test
        @DisplayName("성공적으로 AccessToken과 RefreshToken을 발급한다.")
        void success_get_token() {
            // given
            String accessToken = tokenProvider.createAccessToken(MEMBER_ID);
            String refreshToken = tokenProvider.createRefreshToken(MEMBER_ID);

            accessTokenService.saveToken(AUTH_KEY, accessToken, Duration.ofHours(1));
            refreshTokenService.saveToken(MEMBER_ID, refreshToken, Duration.ofHours(1));

            // when
            TokenResponse tokenResponse = authService.getAccessTokenByAuthKey(AUTH_KEY);

            // then
            assertAll(
                    () -> assertThat(tokenResponse.accessToken()).isEqualTo(accessToken),
                    () -> assertThat(tokenResponse.refreshToken()).isEqualTo(refreshToken)
            );
        }

        @Test
        @DisplayName("저장된 accesstoken이 없으면 오류가 발생한다.")
        void fail_not_found_access_token() {
            // when & then
            assertThatThrownBy(() -> authService.getAccessTokenByAuthKey(AUTH_KEY))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(OAuth2ErrorCode.NOT_FOUND_AUTH_INFO.getMessage());
        }

        @Test
        @DisplayName("AccessToken이 아닌 경우 오류가 발생한다.")
        void fail_is_not_access_token() {
            // given
            String wrongToken = tokenProvider.createRefreshToken(MEMBER_ID);
            accessTokenService.saveToken(AUTH_KEY, wrongToken, Duration.ofHours(1));

            // when & then
            assertThatThrownBy(() -> authService.getAccessTokenByAuthKey(AUTH_KEY))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(JwtErrorCode.TOKEN_INVALID.getMessage());
        }

        @Test
        @DisplayName("RefreshToken이 저장되지 않은 경우 오류가 발생한다.")
        void fail_is_now_saved_token() {
            // given
            String accessToken = tokenProvider.createAccessToken(MEMBER_ID);
            accessTokenService.saveToken(AUTH_KEY, accessToken, Duration.ofHours(1));

            // when & then
            assertThatThrownBy(() -> authService.getAccessTokenByAuthKey(AUTH_KEY))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(JwtErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("로그아웃 테스트")
    class Logout {

        @Test
        @DisplayName("로그아웃에 성공한다.")
        void success_logout() {
            // given
            String refreshToken = tokenProvider.createRefreshToken(MEMBER_ID);
            refreshTokenService.saveToken(MEMBER_ID, refreshToken, Duration.ofHours(1));

            // when
            LogoutResponse logoutResponse = authService.logout();

            // then
            assertAll(
                    () -> assertThat(logoutResponse.isSuccess()).isTrue(),
                    () -> assertThat(logoutResponse.message()).isEqualTo("성공적으로 로그아웃 되었습니다.")
            );
        }

        @Test
        @DisplayName("RefreshToken이 누락되면 오류가 발생한다.")
        void fail_not_found_refresh_token() {
            // when & then
            assertThatThrownBy(() -> authService.logout())
                    .isInstanceOf(DomainException.class)
                    .hasMessage(JwtErrorCode.REFRESH_TOKEN_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("RefreshToken 재발행 테스트")
    class ReissueToken {

        @Test
        @DisplayName("RefreshToken 재발행에 성공한다.")
        void success_refreshToken_reissue() {
            // given
            String oldRefreshToken = tokenProvider.createRefreshToken(MEMBER_ID);

            refreshTokenService.saveToken(
                    MEMBER_ID,
                    oldRefreshToken,
                    Duration.ofHours(1)
            );

            // when
            TokenResponse tokenResponse = authService.reissue(oldRefreshToken);

            // then
            assertAll(
                    () -> assertThat(tokenResponse.accessToken()).isNotBlank(),
                    () -> {
                        String stored = refreshTokenService.getToken(MEMBER_ID);
                        assertThat(stored).isEqualTo(tokenResponse.refreshToken());
                    }
            );
        }

        @Test
        @DisplayName("Type이 잘못된 경우 오류를 반환한다.")
        void fail_is_not_valid_token() {
            // given
            String wrongToken = tokenProvider.createAccessToken(MEMBER_ID);

            // when & then
            assertThatThrownBy(() -> authService.reissue(wrongToken))
                    .isInstanceOf(DomainException.class)
                    .hasMessage(JwtErrorCode.TOKEN_INVALID.getMessage());
        }

        @Test
        @DisplayName("레디스에 저장된 refreshToken과 다른 경우 오류가 발생한다.")
        void fail_is_not_stored_token() {
            // given
            String realToken = tokenProvider.createRefreshToken(MEMBER_ID);
            String fakeToken = UUID.randomUUID().toString();

            refreshTokenService.saveToken(
                    MEMBER_ID,
                    realToken,
                    Duration.ofHours(1)
            );

            // when & then
            assertThatThrownBy(() -> authService.reissue(fakeToken))
                    .isInstanceOf(MalformedJwtException.class);
        }
    }
}