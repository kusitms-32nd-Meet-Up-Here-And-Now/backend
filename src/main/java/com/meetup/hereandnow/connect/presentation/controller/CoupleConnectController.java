package com.meetup.hereandnow.connect.presentation.controller;

import com.meetup.hereandnow.connect.application.CoupleConnectingService;
import com.meetup.hereandnow.connect.dto.request.CoupleConnectingRequestDto;
import com.meetup.hereandnow.connect.dto.response.CoupleConnectingResponseDto;
import com.meetup.hereandnow.connect.presentation.swagger.CoupleConnectSwagger;
import com.meetup.hereandnow.core.presentation.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/couple")
@RequiredArgsConstructor
public class CoupleConnectController implements CoupleConnectSwagger {

    private final CoupleConnectingService coupleConnectingService;

    @Override
    @PostMapping("/connect")
    public ResponseEntity<RestResponse<CoupleConnectingResponseDto>> connectCouple(
            @RequestBody CoupleConnectingRequestDto coupleConnectingRequestDto
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        coupleConnectingService.connectCouple(coupleConnectingRequestDto)
                )
        );
    }
}
