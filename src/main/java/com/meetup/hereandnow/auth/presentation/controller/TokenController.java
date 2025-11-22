package com.meetup.hereandnow.auth.presentation.controller;

import com.meetup.hereandnow.auth.dto.response.TokenResponse;
import com.meetup.hereandnow.auth.infrastructure.jwt.TokenProvider;
import com.meetup.hereandnow.core.presentation.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
@Profile("!prod")
public class TokenController {

    private final TokenProvider tokenProvider;

    @GetMapping("/{memberId}")
    public ResponseEntity<RestResponse<TokenResponse>> getToken(
            @PathVariable Long memberId
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        new TokenResponse(
                                tokenProvider.createAccessToken(memberId),
                                tokenProvider.createRefreshToken(memberId)
                        )
                )
        );
    }
}
