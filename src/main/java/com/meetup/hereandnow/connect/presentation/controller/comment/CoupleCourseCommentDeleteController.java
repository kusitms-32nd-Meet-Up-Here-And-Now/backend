package com.meetup.hereandnow.connect.presentation.controller.comment;

import com.meetup.hereandnow.connect.application.comment.CoupleCourseCommentDeleteService;
import com.meetup.hereandnow.connect.presentation.swagger.comment.CoupleCourseCommentDeleteSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/couple/comment")
@RequiredArgsConstructor
public class CoupleCourseCommentDeleteController implements CoupleCourseCommentDeleteSwagger {

    private final CoupleCourseCommentDeleteService coupleCourseCommentDeleteService;

    @Override
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteImageComment(
            @PathVariable Long commentId
    ) {
        coupleCourseCommentDeleteService.deleteImageComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
