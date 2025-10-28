package com.meetup.hereandnow.scrap.presentation.controller;

import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.scrap.application.facade.ScrapFacade;
import com.meetup.hereandnow.scrap.dto.response.ScrapResponseDto;
import com.meetup.hereandnow.scrap.presentation.swagger.ScrapSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/scrap")
public class ScrapController implements ScrapSwagger {

    private final ScrapFacade scrapFacade;

    @Override
    @PostMapping("/course/{courseId}")
    public ResponseEntity<RestResponse<ScrapResponseDto>> scrapCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        scrapFacade.toggleScrapCourse(courseId)
                )
        );
    }

    @Override
    @PostMapping("/place/{placeId}")
    public ResponseEntity<RestResponse<ScrapResponseDto>> scrapPlace(@PathVariable Long placeId) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        scrapFacade.toggleScrapPlace(placeId)
                )
        );
    }
}
