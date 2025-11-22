package com.meetup.hereandnow.course.presentation.controller;

import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.course.application.facade.CourseViewFacade;
import com.meetup.hereandnow.course.dto.response.CourseCardWithCommentDto;
import com.meetup.hereandnow.course.dto.response.CourseSearchWithCommentDto;
import com.meetup.hereandnow.course.presentation.swagger.CourseDiscoverSwagger;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/discover/course")
public class CourseDiscoverController implements CourseDiscoverSwagger {

    private final CourseViewFacade courseViewFacade;

    @Override
    @GetMapping
    public ResponseEntity<RestResponse<List<CourseCardWithCommentDto>>> getRecentCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        courseViewFacade.getRecentCourses(page, size)
                )
        );
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<RestResponse<CourseSearchWithCommentDto>> getFilteredCourses(
            @RequestParam(defaultValue = "0") @Parameter(description = "페이지 번호", example = "0")
            int page,
            @RequestParam(defaultValue = "20") @Parameter(description = "페이지당 개수", example = "20")
            int size,
            @RequestParam(required = false) @Parameter(description = "별점", example = "4")
            Integer rating,
            @RequestParam(required = false) @Parameter(description = "키워드 리스트", example = "[\"도쿄\", \"서울\"]")
            List<String> keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "시작 날짜", example = "2025-01-01")
            LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @Parameter(description = "끝 날짜", example = "2025-12-31")
            LocalDate endDate,
            @RequestParam(required = false) @Parameter(description = "누구와 함께했는지", example = "연인")
            String with,
            @RequestParam(required = false) @Parameter(description = "지역", example = "강남")
            String region,
            @RequestParam(required = false) @Parameter(description = "업종 코드 리스트", example = "[\"AT4\", \"CT1\"]")
            List<String> placeCode,
            @RequestParam(required = false) @Parameter(description = "태그 리스트", example = "[\"사진 찍기 좋아요\", \"음식이 맛있어요\"]")
            List<String> tag
    ) {
        return ResponseEntity.ok(
                new RestResponse<>(
                        courseViewFacade.getFilteredCourses(page, size, rating, keyword, startDate, endDate, with, region, placeCode, tag)
                )
        );
    }
}
