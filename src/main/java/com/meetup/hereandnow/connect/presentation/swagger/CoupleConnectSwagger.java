package com.meetup.hereandnow.connect.presentation.swagger;

import com.meetup.hereandnow.connect.dto.response.CoupleConnectingResponseDto;
import com.meetup.hereandnow.core.config.swagger.ApiErrorCode;
import com.meetup.hereandnow.core.presentation.RestResponse;
import com.meetup.hereandnow.member.exception.CoupleErrorCode;
import com.meetup.hereandnow.member.exception.MemberErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Connecting", description = "커넥팅 화면 관련 API")
public interface CoupleConnectSwagger {

    @Operation(
            summary = "커넥팅 - 나에게 온 커플 요청 확인 API",
            description = "현재 나에게 온 커플 요청을 확인합니다. 없는 경우 null로 반환합니다.",
            operationId = "GET /couple/requests/pending"
    )
    ResponseEntity<RestResponse<CoupleConnectingResponseDto>> getPendingRequest();

    @Operation(
            summary = "커넥팅 - 커플 연결 요청 보내기",
            description = "커플 연결을 요청하고자 하는 상대방에게 커플 연결 요청을 보냅니다",
            operationId = "POST /couple/requests"
    )
    @ApiErrorCode({MemberErrorCode.class, CoupleErrorCode.class})
    ResponseEntity<RestResponse<CoupleConnectingResponseDto>> sendRequest(
            @RequestParam(name = "상대방 username") String opponentUsername
    );

    @Operation(
            summary = "커넥팅 - 커플 요청 수락하기",
            description = "나에게 온 커플 요청을 수락합니다. 전달 받은 커플 식별자를 통해 요청을 수락합니다.",
            operationId = "POST /couple/requests/{coupleId}/approve"
    )
    ResponseEntity<RestResponse<CoupleConnectingResponseDto>> approveRequest(
            @PathVariable(name = "커플 식별자") Long coupleId
    );

    @Operation(
            summary = "커넥팅 - 커플 요청 거절하기",
            description = "나에게 온 커플 요청을 거절합니다. 전달 받은 커플 식별자를 통해 요청을 거절합니다.",
            operationId = "DELETE /couple/requests/{coupleId}/reject"
    )
    ResponseEntity<Void> rejectRequest(
            @PathVariable Long coupleId
    );
}
