package com.meetup.hereandnow.course.presentation.controller;

import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.course.application.facade.CourseViewFacade;
import com.meetup.hereandnow.course.dto.response.CourseCardResponseDto;
import com.meetup.hereandnow.course.dto.response.CourseDetailsResponseDto;
import com.meetup.hereandnow.course.presentation.swagger.CourseViewSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/course")
public class CourseViewController implements CourseViewSwagger {

    private final CourseViewFacade courseViewFacade;

    @Override
    @GetMapping("/{courseId}")
    public ResponseEntity<RestResponse<CourseDetailsResponseDto>> getCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        courseViewFacade.getCourseDetails(courseId)
                )
        );
    }

    @Override
    @GetMapping("/home/recommended")
    public ResponseEntity<RestResponse<List<CourseCardResponseDto>>> getRecommendedCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "scraps") String sort,
            @RequestParam(defaultValue = "37.566585446882") double lat,
            @RequestParam(defaultValue = "126.978203640984") double lon
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        courseViewFacade.getRecommendedCourses(page, size, sort, lat, lon)
                )
        );
    }
}
