package com.meetup.hereandnow.archive.presentation.swagger;

import com.meetup.hereandnow.archive.dto.response.CourseFolderResponseDto;
import com.meetup.hereandnow.archive.dto.response.RecentArchiveResponseDto;
import com.meetup.hereandnow.core.presentation.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Archive", description = "아카이빙 관련 컨트롤러")
public interface ArchiveSwagger {

    @Operation(
            summary = "가장 최신 아카이빙 폴더 API",
            operationId = "/archive/recent"
    )
    ResponseEntity<RestResponse<RecentArchiveResponseDto>> getCreatedCourse();

    @Operation(
            summary = "내가 생성한 코스 폴더 리스트 API",
            operationId = "/archive/created"
    )
    ResponseEntity<RestResponse<List<CourseFolderResponseDto>>> getMyCreatedCourses(
            @RequestParam int page,
            @RequestParam int size
    );

    @Operation(
            summary = "아카이빙 폴더 검색(필터링) API",
            operationId = "/archive/search"
    )
    ResponseEntity<RestResponse<List<CourseFolderResponseDto>>> getFilteredArchiveCourses(
            @RequestParam int page,
            @RequestParam int size,

            @Schema(description = "별점", example = "5")
            Integer rating,

            @Schema(description = "키워드 리스트", example = "선물, 기념일")
            List<String> keyword,

            @Schema(description = "검색 시작 날짜", example = "2025-11-01")
            LocalDate startDate,

            @Schema(description = "검색 끝 날짜", example = "2025-11-30")
            LocalDate endDate,

            @Schema(description = "누구와 함께했는지", example = "연인")
            String with,

            @Schema(description = "지역", example = "강남")
            String region,

            @Schema(description = "업종 코드 리스트", example = "AT4, CT1")
            List<String> placeCode,

            @Schema(description = "태그 리스트", example = "사진 찍기 좋아요, 음식이 맛있어요")
            List<String> tag
    );
}
