package com.meetup.hereandnow.course.presentation.controller;

import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.course.application.service.comment.CourseCommentSaveService;
import com.meetup.hereandnow.course.dto.request.CourseCommentSaveRequestDto;
import com.meetup.hereandnow.course.presentation.swagger.CourseCommentSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/course/comment")
@RequiredArgsConstructor
public class CourseCommentController implements CourseCommentSwagger {

    private final CourseCommentSaveService courseCommentSaveService;

    @Override
    @PostMapping
    public ResponseEntity<RestResponse<Void>> saveComment(
            @RequestBody CourseCommentSaveRequestDto courseCommentSaveRequestDto
    ) {
        courseCommentSaveService.saveCourseComment(courseCommentSaveRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }
}
