package com.meetup.hereandnow.place.presentation.swagger;

import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.place.dto.response.PlaceCardResponseDto;
import com.meetup.hereandnow.place.dto.response.PlacePointResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Place", description = "장소 관련 API")
public interface PlaceHomeSwagger {

    @Operation(
            summary = "홈 - 광고 추천 장소 리스트 API",
            description = "지도에 타원형 및 핀으로 표시되는, DB 내의 현재 위치 근처 장소들이 랜덤으로 반환됩니다.",
            operationId = "GET /place/home/ads"
    )
    ResponseEntity<RestResponse<List<PlacePointResponseDto>>> getAdPlaces(
            @RequestParam double lat,
            @RequestParam double lon
    );

    @Operation(
            summary = "홈 - 추천 장소 리스트 API",
            description = "근처 추천 장소 리스트를 반환합니다. sort는 recent, scraps, reviews 입력 가능합니다.",
            operationId = "GET /place/home/recommended"
    )
    ResponseEntity<RestResponse<List<PlaceCardResponseDto>>> getRecommendedPlaces(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sort,
            @RequestParam double lat,
            @RequestParam double lon
    );
}
