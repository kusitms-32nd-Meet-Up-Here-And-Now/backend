package com.meetup.hereandnow.connect.presentation.controller;

import com.meetup.hereandnow.connect.application.CoupleConnectingService;
import com.meetup.hereandnow.connect.dto.response.CoupleConnectingResponseDto;
import com.meetup.hereandnow.connect.presentation.swagger.CoupleConnectSwagger;
import com.meetup.hereandnow.core.presentation.RestResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/couple")
@RequiredArgsConstructor
public class CoupleConnectController implements CoupleConnectSwagger {

    private final CoupleConnectingService coupleConnectingService;

    @Override
    @GetMapping("/requests/pending")
    public ResponseEntity<RestResponse<CoupleConnectingResponseDto>> getPendingRequest() {
        Optional<CoupleConnectingResponseDto> response = coupleConnectingService.getPendingRequest();

        return response.map(coupleConnectingResponseDto -> ResponseEntity.ok(
                new RestResponse<>(coupleConnectingResponseDto)
        )).orElseGet(() -> ResponseEntity.ok(
                new RestResponse<>(null)
        ));

    }

    @Override
    @PostMapping("/requests")
    public ResponseEntity<RestResponse<CoupleConnectingResponseDto>> sendRequest(
            @RequestParam String opponentUsername
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new RestResponse<>(
                                coupleConnectingService.sendRequest(opponentUsername)
                        )
                );
    }

    @PostMapping("/requests/{coupleId}/approve")
    public ResponseEntity<RestResponse<CoupleConnectingResponseDto>> approveRequest(
            @PathVariable Long coupleId
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        coupleConnectingService.approveRequest(coupleId)
                )
        );
    }

    @DeleteMapping("/requests/{coupleId}/reject")
    public ResponseEntity<Void> rejectRequest(
            @PathVariable Long coupleId
    ) {
        coupleConnectingService.rejectRequest(coupleId);
        return ResponseEntity.noContent().build();
    }
}
