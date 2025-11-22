package com.meetup.hereandnow.core.infrastructure.filter;

import com.meetup.hereandnow.auth.application.jwt.AccessTokenService;
import com.meetup.hereandnow.auth.application.jwt.RefreshTokenService;
import com.meetup.hereandnow.auth.infrastructure.jwt.JwtProperties;
import com.meetup.hereandnow.auth.infrastructure.jwt.TokenProvider;
import com.meetup.hereandnow.core.infrastructure.security.CustomUserDetails;
import com.meetup.hereandnow.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.mock.web.MockHttpServletResponse;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OAuth2SuccessHandlerTest {

    @Mock private TokenProvider tokenProvider;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private AccessTokenService accessTokenService;
    @Mock private JwtProperties jwtProperties;
    @Mock private Authentication authentication;
    @Mock private HttpServletRequest request;

    private OAuth2SuccessHandler successHandler;
    private MockHttpServletResponse response;
    private Member member;

    @BeforeEach
    void setUp() throws Exception {
        member = Member.builder()
                .id(1L)
                .email("test@test.com")
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(member, Map.of());
        response = new MockHttpServletResponse();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtProperties.refreshExp()).thenReturn(604800);

        successHandler = new OAuth2SuccessHandler(tokenProvider, refreshTokenService, accessTokenService, jwtProperties);

        Field redirectUriField = OAuth2SuccessHandler.class.getDeclaredField("redirectUri");
        redirectUriField.setAccessible(true);
        redirectUriField.set(successHandler, "http://localhost:3000/callback");
    }

    @Test
    @DisplayName("토큰을 생성하고 프론트 url로 리다이렉트 된다.")
    void onAuthenticationSuccess_ShouldGenerateTokensAndRedirect() throws Exception {
        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        when(tokenProvider.createAccessToken(member)).thenReturn(accessToken);
        when(tokenProvider.createRefreshToken(member.getId())).thenReturn(refreshToken);

        successHandler.onAuthenticationSuccess(request, response, authentication);

        verify(refreshTokenService).saveToken(eq(member.getId()), eq(refreshToken), any(Duration.class));
        ArgumentCaptor<String> authCodeCaptor = ArgumentCaptor.forClass(String.class);
        verify(accessTokenService).saveToken(authCodeCaptor.capture(), eq(accessToken), any(Duration.class));

        String redirectUrl = response.getRedirectedUrl();
        assertThat(redirectUrl).startsWith("http://localhost:3000/callback?code=");
        assertThat(redirectUrl).endsWith(authCodeCaptor.getValue());
    }
}