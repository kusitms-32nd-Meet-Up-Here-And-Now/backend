package com.meetup.hereandnow.course.dto.response;

import com.meetup.hereandnow.pin.dto.PinDirnameDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CourseSaveResponseDto(

        @Schema(description = "메타데이터 정보 저장 uuid", example = "8c442ee0-4790-4f17-a530-f7f32c6e0aa3")
        String courseKey,

        List<PinDirnameDto> pinDirname
) {
}
