package com.meetup.hereandnow.place.presentation.controller;

import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.place.application.service.PlaceDetailService;
import com.meetup.hereandnow.place.dto.response.PlaceInfoResponseDto;
import com.meetup.hereandnow.place.presentation.swagger.PlaceDetailSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/place")
@RequiredArgsConstructor
public class PlaceDetailController implements PlaceDetailSwagger {

    private final PlaceDetailService placeDetailService;

    @Override
    @GetMapping("/{placeId}")
    public ResponseEntity<RestResponse<PlaceInfoResponseDto>> getPlaceDetail(
            @PathVariable Long placeId
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        placeDetailService.getPlaceDetail(placeId)
                )
        );
    }
}
