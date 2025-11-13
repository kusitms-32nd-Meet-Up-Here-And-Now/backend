package com.meetup.hereandnow.place.presentation.controller;

import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.place.application.facade.PlaceViewFacade;
import com.meetup.hereandnow.place.dto.response.PlaceCardResponseDto;
import com.meetup.hereandnow.place.dto.response.PlacePointResponseDto;
import com.meetup.hereandnow.place.presentation.swagger.PlaceHomeSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/place/home")
@RequiredArgsConstructor
public class PlaceHomeController implements PlaceHomeSwagger {

    private final PlaceViewFacade placeViewFacade;

    @Override
    @GetMapping("/ads")
    public ResponseEntity<RestResponse<List<PlacePointResponseDto>>> getAdPlaces(
            @RequestParam(defaultValue = "37.566585446882") double lat,
            @RequestParam(defaultValue = "126.978203640984") double lon
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        placeViewFacade.getAdPlaces(lat, lon)
                )
        );
    }

    @Override
    @GetMapping("/recommended")
    public ResponseEntity<RestResponse<List<PlaceCardResponseDto>>> getRecommendedPlaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "scraps") String sort,
            @RequestParam(defaultValue = "37.566585446882") double lat,
            @RequestParam(defaultValue = "126.978203640984") double lon
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        placeViewFacade.getRecommendedPlaces(page, size, sort, lat, lon)
                )
        );
    }
}
