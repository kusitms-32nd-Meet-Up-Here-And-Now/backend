package com.meetup.hereandnow.place.presentation.swagger;

import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.place.dto.response.PlaceCardMarkerResponseDto;
import com.meetup.hereandnow.place.dto.response.PlaceSearchResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Discover(Map)", description = "둘러보기(지도 화면) 관련 API")
public interface PlaceDiscoverSwagger {

    @Operation(
            summary = "지도 - 광고 추천 장소 리스트 API",
            description = "지도 첫 화면에 카드 형식으로 표시되는, 현재 위치 근처 장소들이 랜덤으로 반환됩니다.",
            operationId = "GET /discover/place/ads"
    )
    ResponseEntity<RestResponse<List<PlaceCardMarkerResponseDto>>> getAdPlaces(
            @RequestParam(defaultValue = "37.566585446882") @Parameter(description = "현재 위도", example = "37.566585446882")
            double lat,
            @RequestParam(defaultValue = "126.978203640984") @Parameter(description = "현재 경도", example = "126.978203640984")
            double lon
    );

    @Operation(
            summary = "지도 - 장소 검색 API",
            operationId = "GET /discover/place/search",
            description = "selectedFilters는 검색 결과를 얻기 위해 어떤 필터가 적용되었는지를 나타냅니다. " +
                    "적용되지 않은 필터는 null로 나타납니다.<br>" +
                    "filteredPlaces는 검색 결과 리스트가 리턴됩니다."
    )
    ResponseEntity<RestResponse<PlaceSearchResponseDto>> getFilteredPlaces(
            @RequestParam(defaultValue = "0") @Parameter(description = "페이지 번호", example = "0")
            int page,
            @RequestParam(defaultValue = "2") @Parameter(description = "페이지당 개수", example = "2")
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
