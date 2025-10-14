package com.meetup.hereandnow.core.infrastructure.filter;

import com.meetup.hereandnow.auth.exception.JwtErrorCode;
import com.meetup.hereandnow.auth.infrastructure.jwt.TokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final TokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    private static final List<String> WHITELIST = List.of(
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/docs/ncp-api.json",
            "/test/**",
            "/auth/token"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (isWhitelistedPath(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);
        if (StringUtils.hasText(token) && tokenProvider.isValid(token)) {
            Authentication auth = createAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }

    // 토큰 검사해야 할 URI인지 확인
    private boolean isWhitelistedPath(String requestUri) {
        return WHITELIST.stream().anyMatch(whitelistedUri -> antPathMatcher.match(whitelistedUri, requestUri));
    }

    // 요청 헤더에서 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
            return token.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    // 토큰으로 Authentication 객체 반환
    private Authentication createAuthentication(String token) {
        Claims claims = tokenProvider.resolveTokenClaims(token);
        if (claims.get("type").equals("Refresh")) {
            throw JwtErrorCode.TOKEN_INVALID.toException();
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}