package com.meetup.hereandnow.scrap.presentation.swagger;

import com.meetup.hereandnow.core.config.swagger.ApiErrorCode;
import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.course.dto.response.CourseCardResponseDto;
import com.meetup.hereandnow.place.dto.response.PlaceCardResponseDto;
import com.meetup.hereandnow.scrap.dto.response.ScrapResponseDto;
import com.meetup.hereandnow.scrap.exception.ScrapErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Scrap", description = "저장(좋아요 모음) 관련 컨트롤러")
public interface ScrapSwagger {

    @Operation(
            summary = "코스 스크랩/취소 API",
            operationId = "/scrap/course/{courseId}",
            description = "이미 스크랩된 코스라면 스크랩 취소 및 deleted=\"true\" 응답이 옵니다.<br>" +
                    "아니라면 코스가 스크랩되고 해당 id, 사용자 id, 스크랩된 id, deleted=\"false\"가 전달됩니다."
    )
    @ApiErrorCode({ScrapErrorCode.class})
    ResponseEntity<RestResponse<ScrapResponseDto>> scrapCourse(
            @PathVariable Long courseId
    );

    @Operation(
            summary = "장소 스크랩/취소 API",
            operationId = "/scrap/place/{placeId}",
            description = "이미 스크랩된 장소라면 스크랩 취소 및 deleted=\"true\" 응답이 옵니다.<br>" +
                    "아니라면 장소가 스크랩되고 해당 id, 사용자 id, 스크랩된 id, deleted=\"false\"가 전달됩니다."
    )
    @ApiErrorCode({ScrapErrorCode.class})
    ResponseEntity<RestResponse<ScrapResponseDto>> scrapPlace(
            @PathVariable Long placeId
    );

    @Operation(
            summary = "저장한 코스 목록 보기 API",
            operationId = "/scrap/course",
            description = "정렬 시, sort 값으로 아래 2가지 문자열 중 하나를 전달 가능합니다.<br>" +
                    "1. recent (최신순)<br>" +
                    "2. scraps (저장 많은 순)"
    )
    ResponseEntity<RestResponse<List<CourseCardResponseDto>>> getScrappedCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "recent") String sort
    );

    @Operation(
            summary = "저장한 장소 목록 보기 API",
            operationId = "/scrap/place",
            description = "정렬 시, sort 값으로 아래 3가지 문자열 중 하나를 전달 가능합니다.<br>" +
                    "1. recent (최신순)<br>" +
                    "2. scraps (저장 많은 순)<br>" +
                    "3. reviews (리뷰 많은 순)"
    )
    ResponseEntity<RestResponse<List<PlaceCardResponseDto>>> getScrappedPlaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "recent") String sort
    );
}
