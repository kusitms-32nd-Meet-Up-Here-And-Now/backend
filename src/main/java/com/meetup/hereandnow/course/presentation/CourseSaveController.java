package com.meetup.hereandnow.course.presentation;

import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.course.application.facade.CourseSaveFacade;
import com.meetup.hereandnow.course.dto.request.CommitSaveCourseRequestDto;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import com.meetup.hereandnow.course.dto.response.CommitSaveCourseResponseDto;
import com.meetup.hereandnow.course.dto.response.CourseSaveResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseSaveController {

    private final CourseSaveFacade courseSaveFacade;

    @PostMapping("/save")
    public ResponseEntity<RestResponse<CourseSaveResponseDto>> courseSave(
            @RequestBody CourseSaveDto courseSaveDto
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        courseSaveFacade.prepareCourseSave(courseSaveDto)
                )
        );
    }

    @PostMapping("/{courseUuid}/commit")
    public ResponseEntity<RestResponse<CommitSaveCourseResponseDto>> commitSaveCourse(
            @PathVariable String courseUuid,
            @RequestBody CommitSaveCourseRequestDto commitSaveCourseRequestDto
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        courseSaveFacade.commitSaveCourse(courseUuid, commitSaveCourseRequestDto)
                )
        );
    }
}
