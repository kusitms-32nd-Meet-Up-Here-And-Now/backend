package com.meetup.hereandnow.connect.presentation.controller.comment;

import com.meetup.hereandnow.connect.application.comment.CoupleCourseCommentReadService;
import com.meetup.hereandnow.connect.dto.response.CoupleCourseCommentResponseDto;
import com.meetup.hereandnow.connect.presentation.swagger.comment.CoupleCourseCommentReadSwagger;
import com.meetup.hereandnow.core.presentation.RestResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/couple/comment")
@RequiredArgsConstructor
public class CoupleCourseCommentReadController implements CoupleCourseCommentReadSwagger {

    private final CoupleCourseCommentReadService coupleCourseCommentReadService;

    @Override
    @GetMapping("/{courseId}")
    public ResponseEntity<RestResponse<List<CoupleCourseCommentResponseDto>>> readComment(
            @PathVariable Long courseId
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        coupleCourseCommentReadService.getComments(courseId)
                )
        );
    }
}
