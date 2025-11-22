package com.meetup.hereandnow.course.presentation.swagger;

import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.course.dto.response.CourseCardWithCommentDto;
import com.meetup.hereandnow.course.dto.response.CourseSearchWithCommentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Discover(Map)", description = "둘러보기(지도 화면) 관련 API")
public interface CourseDiscoverSwagger {

    @Operation(
            summary = "지도 - 최근 등록된 코스 리스트 API",
            description = "둘러보기에서 깃발 진입 시 초기에 보이는 최근 등록된 코스 리스트를 반환합니다.<br>" +
                    "코스에 대한 정보인 courseCard, 댓글에 대한 정보인 comment, 코스 스크랩 여부 scrapped로 구성됩니다.",
            operationId = "GET /discover/course"
    )
    ResponseEntity<RestResponse<List<CourseCardWithCommentDto>>> getRecentCourses(
            @RequestParam(defaultValue = "0") @Parameter(description = "페이지 번호", example = "0")
            int page,
            @RequestParam(defaultValue = "20") @Parameter(description = "페이지당 개수", example = "20")
            int size
    );

    @Operation(
            summary = "지도 - 코스 검색 API",
            operationId = "GET /discover/course/search",
            description = "둘러보기에서 깃발 진입 -> 서치바 클릭 시 코스를 검색할 수 있는 API입니다. <br>" +
                    "selectedFilters는 현재 검색 결과에 어떤 필터가 적용되었는지를 나타냅니다. <br>" +
                    "filteredCourses는 검색 결과입니다. 코스에 대한 정보인 courseCard, 댓글에 대한 정보인 comment, 코스 스크랩 여부 scrapped로 구성됩니다."
    )
    ResponseEntity<RestResponse<CourseSearchWithCommentDto>> getFilteredCourses(
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
    );
}
