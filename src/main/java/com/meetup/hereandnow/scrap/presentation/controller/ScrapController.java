package com.meetup.hereandnow.scrap.presentation.controller;

import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.course.dto.response.CourseCardResponseDto;
import com.meetup.hereandnow.place.dto.response.PlaceCardResponseDto;
import com.meetup.hereandnow.scrap.application.facade.ScrapFacade;
import com.meetup.hereandnow.scrap.dto.response.ScrapResponseDto;
import com.meetup.hereandnow.scrap.presentation.swagger.ScrapSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Override
    @GetMapping("/course")
    public ResponseEntity<RestResponse<List<CourseCardResponseDto>>> getScrappedCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "recent") String sort
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        scrapFacade.getScrappedCourses(page, size, sort)
                )
        );
    }

    @Override
    @GetMapping("/place")
    public ResponseEntity<RestResponse<List<PlaceCardResponseDto>>> getScrappedPlaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "recent") String sort
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        scrapFacade.getScrappedPlaces(page, size, sort)
                )
        );
    }
}
