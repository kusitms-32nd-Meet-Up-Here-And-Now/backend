package com.meetup.hereandnow.archive.presentation.swagger;

import com.meetup.hereandnow.archive.dto.response.CourseCardDto;
import com.meetup.hereandnow.core.config.swagger.ApiErrorCode;
import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.scrap.exception.ScrapErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Archive", description = "아카이브 관련 컨트롤러")
public interface ArchiveSwagger {

    @Operation(
            summary = "내가 만든 추억 조회 API",
            operationId = "/archive/created"
    )
    @ApiErrorCode({ScrapErrorCode.class})
    ResponseEntity<RestResponse<List<CourseCardDto>>> getCreatedCourse(
            @Parameter(description = "페이지 넘버") int page,
            @Parameter(description = "한 페이지당 크기") int size
    );

    @Operation(
            summary = "내가 저장한 코스 조회 API",
            operationId = "/archive/scrapped/course"
    )
    @ApiErrorCode({ScrapErrorCode.class})
    ResponseEntity<RestResponse<List<CourseCardDto>>> getScrappedCourse(
            @Parameter(description = "페이지 넘버") int page,
            @Parameter(description = "한 페이지당 크기") int size
    );

//    @Operation(
//            summary = "내가 저장한 장소 조회 API",
//            operationId = "/archive/scrapped/place"
//    )
//    @ApiErrorCode({ScrapErrorCode.class})
//    ResponseEntity<RestResponse<List<PlaceCardDto>>> getScrappedPlace(
//            @Parameter(description = "페이지 넘버") int page,
//            @Parameter(description = "한 페이지당 크기") int size
//    );
}
