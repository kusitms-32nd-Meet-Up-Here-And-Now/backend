package com.meetup.hereandnow.archive.presentation.swagger;

import com.meetup.hereandnow.archive.dto.response.CourseFolderResponseDto;
import com.meetup.hereandnow.archive.dto.response.RecentArchiveResponseDto;
import com.meetup.hereandnow.core.presentation.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Archive", description = "아카이빙 관련 컨트롤러")
public interface ArchiveSwagger {

    @Operation(
            summary = "가장 최신 아카이빙 폴더 API",
            operationId = "/archive/recent"
    )
    ResponseEntity<RestResponse<RecentArchiveResponseDto>> getCreatedCourse();

    @Operation(
            summary = "내가 생성한 코스 폴더 리스트 API",
            operationId = "/archive/created"
    )
    ResponseEntity<RestResponse<List<CourseFolderResponseDto>>> getMyCreatedCourses(int page, int size);
}
