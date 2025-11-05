package com.meetup.hereandnow.connect.presentation.swagger;

import com.meetup.hereandnow.connect.dto.request.CoupleConnectingRequestDto;
import com.meetup.hereandnow.connect.dto.response.CoupleConnectingResponseDto;
import com.meetup.hereandnow.core.config.swagger.ApiErrorCode;
import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.member.exception.CoupleErrorCode;
import com.meetup.hereandnow.member.exception.MemberErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Connecting", description = "커넥팅 화면 관련 API")
public interface CoupleConnectSwagger {

    @Operation(
            summary = "커넥팅 - 커플 연결 실행",
            description = "커플 연결을 실행합니다. 커플 연결을 하고자 하는 상대방의 username을 입력하여 수행합니다.",
            operationId = "/couple/connect"
    )
    @ApiErrorCode({MemberErrorCode.class, CoupleErrorCode.class})
    ResponseEntity<RestResponse<CoupleConnectingResponseDto>> connectCouple(
            @RequestBody CoupleConnectingRequestDto coupleConnectingRequestDto
    );
}
