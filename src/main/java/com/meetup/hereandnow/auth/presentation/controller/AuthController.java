package com.meetup.hereandnow.auth.presentation.controller;

import com.meetup.hereandnow.auth.application.AuthService;
import com.meetup.hereandnow.auth.dto.request.ReIssueTokenRequest;
import com.meetup.hereandnow.auth.dto.request.TokenIssueRequest;
import com.meetup.hereandnow.auth.dto.response.LogoutResponse;
import com.meetup.hereandnow.auth.dto.response.TokenResponse;
import com.meetup.hereandnow.auth.presentation.AuthSwagger;
import com.meetup.hereandnow.core.presentation.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthSwagger {

    private final AuthService authService;


    @Override
    @PostMapping("/token")
    public ResponseEntity<RestResponse<TokenResponse>> issueToken(
            @RequestBody TokenIssueRequest request
            ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        authService.getAccessTokenByAuthKey(request.authKey())
                )
        );
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<RestResponse<LogoutResponse>> logout() {
        return ResponseEntity.ok(
                new RestResponse<>(
                        authService.logout()
                )
        );
    }

    @Override
    @PostMapping("/re-issue")
    public ResponseEntity<RestResponse<TokenResponse>> reissueToken(
            @RequestBody ReIssueTokenRequest request
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        authService.reissue(request.refreshToken())
                )
        );
    }
}
