package com.meetup.hereandnow.core.infrastructure.filter;

import com.meetup.hereandnow.auth.exception.OAuth2ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Log4j2
@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ){
        try{
            log.warn("로그인 실패 : {}", exception.getLocalizedMessage());

            String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam(
                            "error",
                            URLEncoder.encode(exception.getLocalizedMessage(), StandardCharsets.UTF_8)
                    )
                    .build().toUriString();

            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } catch (IOException e) {
            log.error("에러 발생 : {}", e.getLocalizedMessage());
            throw OAuth2ErrorCode.OAUTH2_REDIRECT_ERROR.toException();
        }
    }
}
