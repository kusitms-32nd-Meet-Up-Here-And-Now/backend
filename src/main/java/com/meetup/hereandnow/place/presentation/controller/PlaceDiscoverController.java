package com.meetup.hereandnow.place.presentation.controller;

import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.place.application.facade.PlaceViewFacade;
import com.meetup.hereandnow.place.dto.response.PlaceCardMarkerResponseDto;
import com.meetup.hereandnow.place.dto.response.PlaceSearchResponseDto;
import com.meetup.hereandnow.place.presentation.swagger.PlaceDiscoverSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/discover/place")
public class PlaceDiscoverController implements PlaceDiscoverSwagger {

    private final PlaceViewFacade placeViewFacade;

    @Override
    @GetMapping("/ads")
    public ResponseEntity<RestResponse<List<PlaceCardMarkerResponseDto>>> getAdPlaces(
            @RequestParam(defaultValue = "37.566585446882") double lat,
            @RequestParam(defaultValue = "126.978203640984") double lon
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        placeViewFacade.getAdPlacesWithMarker(lat, lon)
                )
        );
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<RestResponse<PlaceSearchResponseDto>> getFilteredPlaces(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "2")
            int size,
            @RequestParam(required = false)
            Integer rating,
            @RequestParam(required = false)
            List<String> keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            @RequestParam(required = false)
            String with,
            @RequestParam(required = false)
            String region,
            @RequestParam(required = false)
            List<String> placeCode,
            @RequestParam(required = false)
            List<String> tag
    ) {
        return null;
    }
}
