package com.meetup.hereandnow.connect.presentation.swagger;

import com.meetup.hereandnow.connect.dto.response.CoupleCourseSearchResponseDto;
import com.meetup.hereandnow.connect.dto.response.CoupleRecentArchiveReseponseDto;
import com.meetup.hereandnow.connect.exception.CoupleErrorCode;
import com.meetup.hereandnow.core.config.swagger.ApiErrorCode;
import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.member.exception.MemberErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Connecting-Search", description = "커넥팅 - 검색/폴더 API")
public interface CoupleCourseSearchSwagger {

    @Operation(
            summary = "커넥팅 - 가장 최근에 연인과 다녀온 코스 조회 API",
            description = "가장 최근에 연인과 다녀온 코스 1개를 조회합니다.",
            operationId = "GET /couple/course/recent"
    )
    @ApiErrorCode({CoupleErrorCode.class, MemberErrorCode.class})
    ResponseEntity<RestResponse<CoupleRecentArchiveReseponseDto>> recentSearch();


    @Operation(
            summary = "커넥팅 - 검색 필터 적용하여 폴더 UI 리스트 반환 API",
            description = "검색 필터를 적용하여 폴더 UI에 적용할 수 있도록 코스를 반환하는 API입니다.",
            operationId = "GET /couple/course"
    )
    ResponseEntity<RestResponse<CoupleCourseSearchResponseDto>> searchWithFilter(
            @RequestParam(defaultValue = "0") @Parameter(description = "페이지 번호", example = "0")
            int page,
            @RequestParam(defaultValue = "32") @Parameter(description = "페이지당 개수", example = "32")
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
            @RequestParam(required = false) @Parameter(description = "지역", example = "강남")
            String region,
            @RequestParam(required = false) @Parameter(description = "업종 코드 리스트", example = "[\"AT4\", \"CT1\"]")
            List<String> placeCode,
            @RequestParam(required = false) @Parameter(description = "태그 리스트", example = "[\"사진 찍기 좋아요\", \"음식이 맛있어요\"]")
            List<String> tag
    );
}
