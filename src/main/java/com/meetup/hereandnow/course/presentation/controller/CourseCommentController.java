package com.meetup.hereandnow.course.presentation.controller;

import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.course.application.service.comment.CourseCommentSaveService;
import com.meetup.hereandnow.course.application.service.comment.CourseCommentSearchService;
import com.meetup.hereandnow.course.dto.request.CourseCommentSaveRequestDto;
import com.meetup.hereandnow.course.dto.response.CourseCommentResponseDto;
import com.meetup.hereandnow.course.presentation.swagger.CourseCommentSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/course/comment")
@RequiredArgsConstructor
public class CourseCommentController implements CourseCommentSwagger {

    private final CourseCommentSaveService courseCommentSaveService;
    private final CourseCommentSearchService courseCommentSearchService;

    @Override
    @PostMapping
    public ResponseEntity<RestResponse<Void>> saveComment(
            @RequestBody CourseCommentSaveRequestDto courseCommentSaveRequestDto
    ) {
        courseCommentSaveService.saveCourseComment(courseCommentSaveRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RestResponse<>(null));
    }

    @Override
    @GetMapping("/{courseId}")
    public ResponseEntity<RestResponse<CourseCommentResponseDto>> getCommentList(
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        courseCommentSearchService.getCommentList(courseId)
                )
        );
    }
}
