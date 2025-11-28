package com.meetup.hereandnow.course.presentation.swagger;

import com.meetup.hereandnow.core.config.swagger.ApiErrorCode;
import com.meetup.hereandnow.course.exception.CourseErrorCode;
import com.meetup.hereandnow.member.exception.MemberErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Course", description = "데이트 코스 관련 컨트롤러")
public interface CourseDeleteSwagger {

    @Operation(
            summary = "코스 삭제 API",
            description = "코스 식별자를 사용하여 아카이빙에서의 본인의 코스를 삭제합니다. 204 No content로 반환됩니다.",
            operationId = "DELETE /course/{courseId}"
    )
    @ApiErrorCode({MemberErrorCode.class, CourseErrorCode.class})
    ResponseEntity<Void> deleteCourse(
            @PathVariable @Parameter(description = "코스 식별자", example = "1") Long courseId
    );
}
