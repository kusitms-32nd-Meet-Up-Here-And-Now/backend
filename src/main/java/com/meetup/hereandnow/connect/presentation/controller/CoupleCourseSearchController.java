package com.meetup.hereandnow.connect.presentation.controller;

import com.meetup.hereandnow.connect.application.CoupleConnectingSearchService;
import com.meetup.hereandnow.connect.dto.response.CoupleCourseSearchResponseDto;
import com.meetup.hereandnow.connect.dto.response.CoupleRecentArchiveReseponseDto;
import com.meetup.hereandnow.connect.presentation.swagger.CoupleCourseSearchSwagger;
import com.meetup.hereandnow.core.presentation.RestResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/couple/search")
@RequiredArgsConstructor
public class CoupleCourseSearchController implements CoupleCourseSearchSwagger {

    private final CoupleConnectingSearchService coupleConnectingSearchService;

    @Override
    @GetMapping("/recent")
    public ResponseEntity<RestResponse<CoupleRecentArchiveReseponseDto>> recentSearch() {
        return ResponseEntity.ok(
                new RestResponse<CoupleRecentArchiveReseponseDto>(
                        coupleConnectingSearchService.getRecentArchive()
                )
        );
    }

    @Override
    @GetMapping
    public ResponseEntity<RestResponse<CoupleCourseSearchResponseDto>> searchWithFilter(
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
            String region,
            @RequestParam(required = false)
            List<String> placeCode,
            @RequestParam(required = false)
            List<String> tag
    ) {
        return ResponseEntity.ok(
                new RestResponse<CoupleCourseSearchResponseDto>(
                        coupleConnectingSearchService.getCourseFolder(
                                page, size, rating, keyword,
                                startDate, endDate,
                                region, placeCode, tag
                        )
                )
        );
    }
}
