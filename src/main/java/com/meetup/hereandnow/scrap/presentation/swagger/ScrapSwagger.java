package com.meetup.hereandnow.scrap.presentation.swagger;

import com.meetup.hereandnow.core.config.swagger.ApiErrorCode;
import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.scrap.dto.response.ScrapResponseDto;
import com.meetup.hereandnow.scrap.exception.ScrapErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Scrap", description = "스크랩 관련 컨트롤러")
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
}
