package com.meetup.hereandnow.core.presentation.controller;

import com.meetup.hereandnow.core.exception.error.GlobalErrorCode;
import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.core.presentation.swagger.TestSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController implements TestSwagger {

    private final Environment environment;

    @GetMapping("/")
    public ResponseEntity<RestResponse<String>> profileTest() {
        String[] profiles = environment.getActiveProfiles();
        String profile = profiles.length > 0 ? profiles[0] : "unknown";

        return ResponseEntity.ok(new RestResponse<>(profile + " test"));
    }

    @Override
    @GetMapping("/health")
    public ResponseEntity<RestResponse<String>> health() {
        return ResponseEntity.ok(
                new RestResponse<>("OK")
        );
    }

    @Override
    @GetMapping("/error")
    public ResponseEntity<RestResponse<String>> error() {
        throw GlobalErrorCode.INTERNAL_SERVER_ERROR.toException();
    }

}
