package com.meetup.hereandnow.connect.presentation.swagger.info;

import com.meetup.hereandnow.connect.dto.request.CoupleInfoRequestDto;
import com.meetup.hereandnow.core.presentation.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Connecting", description = "커넥팅 화면 관련 API")
public interface CoupleInfoSaveSwagger {

    @Operation(
            summary = "커넥팅 - 커플 정보 변경 API",
            description = "커플 정보를 변경합니다. 정보를 변경하지 않는 곳은 null로 처리해서 보냅니다",
            operationId = "POST /couple/connect"
    )
    ResponseEntity<RestResponse<CoupleInfoRequestDto>> saveCoupleInfo(
            @RequestBody CoupleInfoRequestDto coupleInfoRequestDto
    );
}
