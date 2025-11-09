package com.meetup.hereandnow.connect.application.comment;

import com.meetup.hereandnow.connect.domain.CoupleCourseImageComment;
import com.meetup.hereandnow.connect.domain.CoupleCourseTextComment;
import com.meetup.hereandnow.connect.dto.request.CoupleCourseImageCommentRequestDto;
import com.meetup.hereandnow.connect.dto.request.CoupleCourseTextCommentRequestDto;
import com.meetup.hereandnow.connect.dto.response.CoupleCourseCommentPresignedUrlResponseDto;
import com.meetup.hereandnow.connect.exception.CoupleCourseCommentErrorCode;
import com.meetup.hereandnow.connect.repository.CoupleCourseCommentRepository;
import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageService;
import com.meetup.hereandnow.core.util.SecurityUtils;
import com.meetup.hereandnow.course.domain.entity.Course;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.course.infrastructure.repository.CourseRepository;
import com.meetup.hereandnow.member.domain.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoupleCourseCommentSaveService {

    private final CoupleCourseCommentRepository coupleCourseCommentRepository;
    private final CourseRepository courseRepository;
    private final ObjectStorageService objectStorageService;

    @Transactional
    public void addTextComment(CoupleCourseTextCommentRequestDto dto) {
        Member member = getCurrentMember();
        Course course = getCourse(dto.courseId());

        CoupleCourseTextComment comment = CoupleCourseTextComment.of(course, member, dto.content());

        coupleCourseCommentRepository.save(comment);
    }

    @Transactional
    public void addImageComment(CoupleCourseImageCommentRequestDto dto) {
        Member member = getCurrentMember();
        Course course = getCourse(dto.courseId());

        if (!objectStorageService.exists(dto.objectKey())) {
            throw CoupleCourseCommentErrorCode.NOT_SAVED_IMAGE.toException();
        }

        CoupleCourseImageComment comment = CoupleCourseImageComment.of(course, member, dto.objectKey());

        coupleCourseCommentRepository.save(comment);
    }

    @Transactional
    public CoupleCourseCommentPresignedUrlResponseDto getPresignedDirname(Long courseId) {
        getCurrentMember();
        getCourse(courseId);

        return new CoupleCourseCommentPresignedUrlResponseDto(getDirname(courseId));
    }

    private String getDirname(Long courseId) {
        return "/course/" + courseId + "/couple/comment";
    }

    private Member getCurrentMember() {
        return SecurityUtils.getCurrentMember();
    }

    private Course getCourse(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(CourseErrorCode.NOT_FOUND_COURSE::toException);
    }
}
