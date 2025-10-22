package com.meetup.hereandnow.course.presentation;

import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.course.application.service.save.CourseSaveService;
import com.meetup.hereandnow.course.dto.CourseSaveDto;
import com.meetup.hereandnow.course.dto.response.CourseSaveResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseSaveController {

    private final CourseSaveService courseSaveService;

    @PostMapping("/save")
    public ResponseEntity<RestResponse<CourseSaveResponseDto>> courseSave(
            @RequestBody CourseSaveDto courseSaveDto
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        courseSaveService.courseSave(courseSaveDto)
                )
        );
    }
}
