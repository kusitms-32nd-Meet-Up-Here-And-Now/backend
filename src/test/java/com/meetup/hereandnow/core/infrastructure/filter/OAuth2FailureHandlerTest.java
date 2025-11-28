package com.meetup.hereandnow.core.infrastructure.filter;

import com.meetup.hereandnow.core.exception.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OAuth2FailureHandlerTest {

    private OAuth2FailureHandler oAuth2FailureHandler;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RedirectStrategy redirectStrategy;

    @BeforeEach
    void setUp() {
        oAuth2FailureHandler = new OAuth2FailureHandler();

        ReflectionTestUtils.setField(oAuth2FailureHandler, "redirectUri", "http://localhost:3000/oauth2/redirect");
        oAuth2FailureHandler.setRedirectStrategy(redirectStrategy);
    }

    @DisplayName("인증 실패 시 에러 메시지를 포함하여 리다이렉트한다")
    @Test
    void onAuthenticationFailureSuccess() throws IOException {
        // given
        String errorMessage = "Authentication failed";
        AuthenticationException exception = new AuthenticationException(errorMessage) {};

        String expectedUrl = "http://localhost:3000/oauth2/redirect?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        // when
        oAuth2FailureHandler.onAuthenticationFailure(request, response, exception);

        // then
        verify(redirectStrategy).sendRedirect(request, response, expectedUrl);
    }

    @DisplayName("리다이렉트 중 IOException이 발생하면 OAuth2ProcessingException을 던진다")
    @Test
    void onAuthenticationFailureThrowsException() throws IOException {
        // given
        String errorMessage = "Authentication failed";
        AuthenticationException exception = new AuthenticationException(errorMessage) {};
        String expectedUrl = "http://localhost:3000/oauth2/redirect?error=" + URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        doThrow(new IOException("Redirect failed")).when(redirectStrategy).sendRedirect(request, response, expectedUrl);

        // when & then
        assertThrows(DomainException.class, () -> {
            oAuth2FailureHandler.onAuthenticationFailure(request, response, exception);
        });
    }
}
