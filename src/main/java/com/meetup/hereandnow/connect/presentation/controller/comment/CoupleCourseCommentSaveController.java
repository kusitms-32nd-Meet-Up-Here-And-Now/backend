package com.meetup.hereandnow.connect.presentation.controller.comment;

import com.meetup.hereandnow.connect.application.comment.CoupleCourseCommentSaveService;
import com.meetup.hereandnow.connect.dto.request.CoupleCourseImageCommentRequestDto;
import com.meetup.hereandnow.connect.dto.request.CoupleCourseTextCommentRequestDto;
import com.meetup.hereandnow.connect.dto.response.CoupleCourseCommentPresignedUrlResponseDto;
import com.meetup.hereandnow.connect.presentation.swagger.comment.CoupleCourseCommentSaveSwagger;
import com.meetup.hereandnow.core.presentation.RestResponse;
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
@RequestMapping("/couple/comment")
@RequiredArgsConstructor
public class CoupleCourseCommentSaveController implements CoupleCourseCommentSaveSwagger {

    private final CoupleCourseCommentSaveService coupleCourseCommentSaveService;

    @Override
    @PostMapping("/text")
    public ResponseEntity<Void> saveTextComment(
            @RequestBody CoupleCourseTextCommentRequestDto coupleCourseTextCommentRequestDto
    ) {
        coupleCourseCommentSaveService.addTextComment(coupleCourseTextCommentRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @Override
    @PostMapping("/image")
    public ResponseEntity<Void> saveImageComment(
            @RequestBody CoupleCourseImageCommentRequestDto coupleCourseImageCommentRequestDto
    ) {
        coupleCourseCommentSaveService.addImageComment(coupleCourseImageCommentRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @Override
    @GetMapping("/pre-signed/{courseId}")
    public ResponseEntity<RestResponse<CoupleCourseCommentPresignedUrlResponseDto>> getPresignedUrl(
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        coupleCourseCommentSaveService.getPresignedDirname(courseId)
                )
        );
    }

}
