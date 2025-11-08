package com.meetup.hereandnow.archive.presentation.controller;

import com.meetup.hereandnow.archive.application.facade.ArchiveFacade;
import com.meetup.hereandnow.archive.dto.response.CourseFolderResponseDto;
import com.meetup.hereandnow.archive.dto.response.RecentArchiveResponseDto;
import com.meetup.hereandnow.archive.presentation.swagger.ArchiveSwagger;
import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.course.dto.response.CourseSearchResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/archive")
@Slf4j
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
            @RequestParam(defaultValue = "32") int size
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        archiveFacade.getMyCreatedCourses(page, size)
                )
        );
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<RestResponse<CourseSearchResponseDto>> getFilteredArchiveCourses(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "32")
            int size,
            @RequestParam(required = false)
            Integer rating,
            @RequestParam(required = false)
            List<String> keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            @RequestParam(required = false)
            String with,
            @RequestParam(required = false)
            String region,
            @RequestParam(required = false)
            List<String> placeCode,
            @RequestParam(required = false)
            List<String> tag
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        archiveFacade.getFilteredArchiveCourses(
                                page, size, rating, keyword,
                                startDate, endDate, with,
                                region, placeCode, tag
                        )
                )
        );
    }
}
