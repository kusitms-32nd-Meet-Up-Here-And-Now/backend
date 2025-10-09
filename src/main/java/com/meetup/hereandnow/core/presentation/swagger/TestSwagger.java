package com.meetup.hereandnow.core.presentation.swagger;

import com.meetup.hereandnow.core.presentation.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Test", description = "서버 테스트용 컨트롤러")
public interface TestSwagger {

    @Operation(
            summary = "서버 health check",
            description = "서버 동작 확인 용 API",
            operationId = "/test/health"
    )
    ResponseEntity<RestResponse<String>> health();

    @Operation(
            summary = "에러 응답 확인",
            description = "에러 응답 확인용 API",
            operationId = "/test/error"
    )
    ResponseEntity<RestResponse<String>> error();
}
