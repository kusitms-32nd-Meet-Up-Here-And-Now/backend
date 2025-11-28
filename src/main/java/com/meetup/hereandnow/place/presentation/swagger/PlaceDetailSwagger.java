package com.meetup.hereandnow.place.presentation.swagger;

import com.meetup.hereandnow.core.config.swagger.ApiErrorCode;
import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.place.dto.response.PlaceInfoResponseDto;
import com.meetup.hereandnow.place.exception.PlaceErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Place", description = "장소 관련 API")
public interface PlaceDetailSwagger {

    @Operation(
            summary = "장소 상세 페이지 API",
            description = "장소 상세 정보를 불러옵니다",
            operationId = "GET /place/{placeId}"
    )
    @ApiErrorCode({PlaceErrorCode.class})
    ResponseEntity<RestResponse<PlaceInfoResponseDto>> getPlaceDetail(
            @PathVariable Long placeId
    );
}
