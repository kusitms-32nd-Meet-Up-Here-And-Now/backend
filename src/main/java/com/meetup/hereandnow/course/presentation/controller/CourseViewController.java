package com.meetup.hereandnow.course.presentation.controller;

import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.course.application.facade.CourseViewFacade;
import com.meetup.hereandnow.course.dto.response.CourseDetailsResponseDto;
import com.meetup.hereandnow.course.presentation.swagger.CourseViewSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
