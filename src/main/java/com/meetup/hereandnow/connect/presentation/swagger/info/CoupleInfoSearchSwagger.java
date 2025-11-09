package com.meetup.hereandnow.connect.presentation.swagger.info;

import com.meetup.hereandnow.connect.dto.response.CoupleCourseBannerResponseDto;
import com.meetup.hereandnow.connect.dto.response.CoupleInfoResponseDto;
import com.meetup.hereandnow.core.presentation.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Connecting", description = "커넥팅 화면 관련 API")
public interface CoupleInfoSearchSwagger {

    @Operation(
            summary = "커넥팅 - 커플 정보 조회 API",
            description = "커플 정보를 조회합니다.",
            operationId = "GET /couple/info"
    )
    ResponseEntity<RestResponse<CoupleInfoResponseDto>> getCoupleInfo();

    @Operation(
            summary = "커넥팅 - 커플 배너 리스트",
            description = "커넥팅 화면의 배너를 반환합니다. 무한스크롤로 구현합니다.",
            operationId = "GET /couple/banner"
    )
    ResponseEntity<RestResponse<Slice<CoupleCourseBannerResponseDto>>> getCOnnectingBanner(
            @RequestParam int page,
            @RequestParam int size
    );
}
