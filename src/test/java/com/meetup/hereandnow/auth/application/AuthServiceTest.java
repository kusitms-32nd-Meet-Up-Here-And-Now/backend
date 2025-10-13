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
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;
    @Mock
    private AccessTokenService accessTokenService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private JwtProperties jwtProperties;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        securityUtilsMock = mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        securityUtilsMock.close();
    }

    @Nested
    @DisplayName("AuthKey로 AccessToken 조회 시")
    class GetAccessTokenByAuthKey {

        private final String accessToken = "test-access-token";
        private final String refreshToken = "test-access-token";
        private final String testAuthKey = "test-auth-key";
        private final Long memberId = 1L;

        @Test
        @DisplayName("성공적으로 로그인 된다.")
        void success() {
            // given
            Claims mockClaims = mock(Claims.class);
            given(mockClaims.get("type")).willReturn("Access");
            given(mockClaims.getSubject()).willReturn(String.valueOf(memberId));

            given(accessTokenService.getToken(testAuthKey)).willReturn(accessToken);
            given(tokenProvider.resolveTokenClaims(accessToken)).willReturn(mockClaims);
            given(refreshTokenService.getToken(memberId)).willReturn(refreshToken);

            // when
            TokenResponse tokenResponse = authService.getAccessTokenByAuthKey(testAuthKey);

            // then
            assertThat(tokenResponse.accessToken()).isEqualTo(accessToken);
            assertThat(tokenResponse.refreshToken()).isEqualTo(refreshToken);
        }

        @Test
        @DisplayName("토큰이 저장이 안된 경우 오류가 발생한다.")
        void fail_isNotExistsToken(){
            // given
            given(accessTokenService.getToken(testAuthKey)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> authService.getAccessTokenByAuthKey(testAuthKey))
                    .isInstanceOf(OAuth2ErrorCode.NOT_FOUND_AUTH_INFO.toException().getClass());
        }

        @Test
        @DisplayName("올바르지 않은 accessToken이 저장된 경우 오류가 발생한다.")
        void fail_invalidToken() {
            // given
            Claims mockClaims = mock(Claims.class);
            given(mockClaims.get("type")).willReturn("Refresh");

            given(accessTokenService.getToken(testAuthKey)).willReturn(accessToken);
            given(tokenProvider.resolveTokenClaims(accessToken)).willReturn(mockClaims);

            // when & then
            assertThatThrownBy(() -> authService.getAccessTokenByAuthKey(testAuthKey))
                    .isInstanceOf(JwtErrorCode.TOKEN_INVALID.toException().getClass());
        }

        @Test
        @DisplayName("refreshToken이 레디스에 없는 경우 오류가 발생한다.")
        void fail_isNotExistsRefreshToken() {
            // given
            Claims mockClaims = mock(Claims.class);
            given(mockClaims.get("type")).willReturn("Access");
            given(mockClaims.getSubject()).willReturn(String.valueOf(memberId));

            given(accessTokenService.getToken(testAuthKey)).willReturn(accessToken);
            given(tokenProvider.resolveTokenClaims(accessToken)).willReturn(mockClaims);
            given(refreshTokenService.getToken(memberId)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> authService.getAccessTokenByAuthKey(testAuthKey))
                    .isInstanceOf(JwtErrorCode.REFRESH_TOKEN_NOT_FOUND.toException().getClass());
        }
    }

    @Nested
    @DisplayName("로그아웃 시")
    class Logout {
        private final Long memberId = 1L;
        private final String refreshToken = "testRefreshToken";

        @Test
        @DisplayName("성공적으로 로그아웃된다")
        void success() {
            // given
            Member member = Member.builder().id(memberId).build();
            given(SecurityUtils.getCurrentMember()).willReturn(member);
            given(refreshTokenService.getToken(memberId)).willReturn(refreshToken);

            // when
            LogoutResponse response = authService.logout();

            // then
            verify(refreshTokenService).deleteToken(memberId);
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.message()).isEqualTo("성공적으로 로그아웃 되었습니다.");
        }

        @Test
        @DisplayName("리프레시 토큰이 없으면 예외를 발생시킨다")
        void fail_NoRefreshToken() {
            // given
            Member member = Member.builder().id(memberId).build();
            given(SecurityUtils.getCurrentMember()).willReturn(member);
            given(refreshTokenService.getToken(memberId)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> authService.logout())
                    .isInstanceOf(JwtErrorCode.REFRESH_TOKEN_NOT_FOUND.toException().getClass());
        }
    }

    @Nested
    @DisplayName("토큰 재발급 시")
    class Reissue {
        private final Long memberId = 1L;
        private final String oldRefreshToken = "oldRefreshToken";
        private final String newAccessToken = "newAccessToken";
        private final String newRefreshToken = "newRefreshToken";

        @Test
        @DisplayName("유효한 리프레시 토큰이면 새로운 토큰들을 발급한다")
        void success() {
            // given
            Claims mockClaims = mock(Claims.class);
            given(mockClaims.get("type")).willReturn("Refresh");
            given(mockClaims.getSubject()).willReturn(String.valueOf(memberId));
            given(tokenProvider.resolveTokenClaims(oldRefreshToken)).willReturn(mockClaims);

            given(refreshTokenService.getToken(memberId)).willReturn(oldRefreshToken);
            given(tokenProvider.createAccessToken(memberId)).willReturn(newAccessToken);
            given(tokenProvider.createRefreshToken(memberId)).willReturn(newRefreshToken);
            given(jwtProperties.refreshExp()).willReturn(1000);

            // when
            TokenResponse tokenResponse = authService.reissue(oldRefreshToken);

            // then
            verify(refreshTokenService).saveToken(memberId, newRefreshToken, Duration.ofSeconds(1000L));
            assertThat(tokenResponse.accessToken()).isEqualTo(newAccessToken);
            assertThat(tokenResponse.refreshToken()).isEqualTo(newRefreshToken);
        }

        @Test
        @DisplayName("저장된 리프레시 토큰이 없으면 예외를 발생시킨다")
        void fail_NoStoredRefreshToken() {
            // given
            Claims mockClaims = mock(Claims.class);
            given(mockClaims.get("type")).willReturn("Refresh");
            given(mockClaims.getSubject()).willReturn(String.valueOf(memberId));
            given(tokenProvider.resolveTokenClaims(oldRefreshToken)).willReturn(mockClaims);

            given(refreshTokenService.getToken(memberId)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> authService.reissue(oldRefreshToken))
                    .isInstanceOf(JwtErrorCode.REFRESH_TOKEN_NOT_FOUND.toException().getClass());
        }

        @Test
        @DisplayName("refreshToken이 아닌 경우 예외를 발생시킨다.")
        void fail_isNotExistsClaims() {
            // given
            Claims mockClaims = mock(Claims.class);
            given(mockClaims.get("type")).willReturn("Access");
            given(tokenProvider.resolveTokenClaims(oldRefreshToken)).willReturn(mockClaims);

            // when & then
            assertThatThrownBy(() -> authService.reissue(oldRefreshToken))
                    .isInstanceOf(JwtErrorCode.TOKEN_INVALID.toException().getClass());
        }

        @Test
        @DisplayName("리프레시 토큰이 일치하지 않으면 예외를 발생시킨다")
        void fail_RefreshTokenMismatch() {
            // given
            Claims mockClaims = mock(Claims.class);
            given(mockClaims.get("type")).willReturn("Refresh");
            given(mockClaims.getSubject()).willReturn(String.valueOf(memberId));
            given(tokenProvider.resolveTokenClaims(oldRefreshToken)).willReturn(mockClaims);

            given(refreshTokenService.getToken(memberId)).willReturn("differentRefreshToken");

            // when & then
            assertThatThrownBy(() -> authService.reissue(oldRefreshToken))
                    .isInstanceOf(JwtErrorCode.REFRESH_TOKEN_NOT_FOUND.toException().getClass());
        }
    }
}
