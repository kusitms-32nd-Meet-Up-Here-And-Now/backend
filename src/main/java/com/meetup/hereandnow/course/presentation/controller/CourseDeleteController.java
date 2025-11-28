package com.meetup.hereandnow.course.presentation.controller;

import com.meetup.hereandnow.course.application.service.delete.CourseDeleteService;
import com.meetup.hereandnow.course.presentation.swagger.CourseDeleteSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseDeleteController implements CourseDeleteSwagger {

    private final CourseDeleteService courseDeleteService;

    @Override
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long courseId
    ) {
        courseDeleteService.courseDeleteById(courseId);
        return ResponseEntity.noContent().build();
    }
}
