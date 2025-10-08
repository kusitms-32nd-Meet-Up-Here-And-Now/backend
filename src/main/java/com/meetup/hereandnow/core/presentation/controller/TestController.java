package com.meetup.hereandnow.core.presentation.controller;

import com.meetup.hereandnow.core.exception.error.GlobalErrorCode;
import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.core.presentation.TestSwagger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController implements TestSwagger {

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
