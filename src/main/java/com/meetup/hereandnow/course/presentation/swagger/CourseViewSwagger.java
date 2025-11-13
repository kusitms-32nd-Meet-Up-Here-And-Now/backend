package com.meetup.hereandnow.course.presentation.swagger;

import com.meetup.hereandnow.core.config.swagger.ApiErrorCode;
import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.course.dto.response.CourseCardResponseDto;
import com.meetup.hereandnow.course.dto.response.CourseDetailsResponseDto;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Course", description = "데이트 코스 관련 컨트롤러")
public interface CourseViewSwagger {

    @Operation(
            summary = "코스 상세 조회 (세부 화면) API",
            operationId = "/course/{courseId}"
    )
    @ApiErrorCode({CourseErrorCode.class})
    ResponseEntity<RestResponse<CourseDetailsResponseDto>> getCourse(
            @PathVariable Long courseId
    );

    @Operation(
            summary = "홈 - 추천 코스 리스트 API",
            description = "근처 추천 코스 리스트를 반환합니다. sort는 recent, scraps, reviews 입력 가능합니다.",
            operationId = "/course/home/recommended"
    )
    ResponseEntity<RestResponse<List<CourseCardResponseDto>>> getRecommendedCourses(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sort,
            @Schema(description = "현재 위도", example = "37.5709578373114")
            @RequestParam double lat,
            @Schema(description = "현재 경도", example = "126.977928770123")
            @RequestParam double lon
    );
}
