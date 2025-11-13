package com.meetup.hereandnow.course.presentation.swagger;

import com.meetup.hereandnow.core.config.swagger.ApiErrorCode;
import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.course.dto.response.CourseDetailsResponseDto;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

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
}
