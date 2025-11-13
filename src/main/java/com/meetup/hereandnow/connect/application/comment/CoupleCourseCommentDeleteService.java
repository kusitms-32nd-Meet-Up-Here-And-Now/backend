package com.meetup.hereandnow.connect.application.comment;

import com.meetup.hereandnow.connect.domain.CoupleCourseComment;
import com.meetup.hereandnow.connect.domain.CoupleCourseImageComment;
import com.meetup.hereandnow.connect.exception.CoupleCourseCommentErrorCode;
import com.meetup.hereandnow.connect.infrastructure.repository.CoupleCourseCommentRepository;
import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.member.domain.Member;
import jakarta.transaction.Transactional;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoupleCourseCommentDeleteService {

    private final CoupleCourseCommentRepository coupleCourseCommentRepository;
    private final ObjectStorageService objectStorageService;

    @Transactional
    public void deleteImageComment(Long commentId) {
        Member member = SecurityUtils.getCurrentMember();

        CoupleCourseComment comment = coupleCourseCommentRepository.findById(commentId)
                .orElseThrow(CoupleCourseCommentErrorCode.NOT_FOUND_COMMENT::toException);

        if (!Objects.equals(comment.getMember().getId(), member.getId())) {
            throw CoupleCourseCommentErrorCode.FORBIDDEN_COMMENT_DELETE.toException();
        }

        if (!(comment instanceof CoupleCourseImageComment imageComment)) {
            throw CoupleCourseCommentErrorCode.NOT_IMAGE_COMMENT.toException();
        }

        if (objectStorageService.exists(imageComment.getImageUrl())) {
            objectStorageService.delete(imageComment.getImageUrl());
        }

        coupleCourseCommentRepository.delete(comment);
    }
}
