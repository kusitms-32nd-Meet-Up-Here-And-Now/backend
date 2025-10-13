package com.meetup.hereandnow.core.infrastructure.filter;

import com.meetup.hereandnow.auth.application.jwt.AccessTokenService;
import com.meetup.hereandnow.auth.application.jwt.RefreshTokenService;
import com.meetup.hereandnow.auth.infrastructure.jwt.JwtProperties;
import com.meetup.hereandnow.auth.infrastructure.jwt.TokenProvider;
import com.meetup.hereandnow.core.infrastructure.security.CustomUserDetails;
import com.meetup.hereandnow.member.domain.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final AccessTokenService accessTokenService;
    private final JwtProperties jwtProperties;

    @Value("${oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = customUserDetails.member();

        String accessToken = tokenProvider.createAccessToken(member);
        String refreshToken = tokenProvider.createRefreshToken(member.getId());

        String authCode = UUID.randomUUID().toString();

        refreshTokenService.saveToken(
                member.getId(), refreshToken, Duration.of(jwtProperties.refreshExp(), TimeUnit.SECONDS.toChronoUnit())
        );

        accessTokenService.saveToken(
                authCode, accessToken, Duration.of(120L, TimeUnit.SECONDS.toChronoUnit())
        );

        String callbackUri = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("code", authCode)
                .build().toUriString();

        response.sendRedirect(callbackUri);
    }
}
