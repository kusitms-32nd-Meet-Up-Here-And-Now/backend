package com.meetup.hereandnow.archive.presentation.controller;

import com.meetup.hereandnow.archive.application.facade.ArchiveFacade;
import com.meetup.hereandnow.archive.dto.response.CourseFolderResponseDto;
import com.meetup.hereandnow.archive.dto.response.RecentArchiveResponseDto;
import com.meetup.hereandnow.archive.presentation.swagger.ArchiveSwagger;
import com.meetup.hereandnow.core.presentation.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/archive")
public class ArchiveController implements ArchiveSwagger {

    private final ArchiveFacade archiveFacade;

    @Override
    @GetMapping("/recent")
    public ResponseEntity<RestResponse<RecentArchiveResponseDto>> getCreatedCourse() {
        return ResponseEntity.ok(
                new RestResponse<>(
                        archiveFacade.getRecentArchive()
                )
        );
    }

    @Override
    @GetMapping("/created")
    public ResponseEntity<RestResponse<List<CourseFolderResponseDto>>> getMyCreatedCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        archiveFacade.getMyCreatedCourses(page, size)
                )
        );
    }
}
