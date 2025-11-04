package com.meetup.hereandnow.course.dto.request;

import java.util.List;
import com.meetup.hereandnow.pin.dto.PinImageObjectKeyDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record CommitSaveCourseRequestDto(

        @Schema(description = "커플 코스 이미지 objectKey", example = "[\"/course/{courseUuid}/image/32de1cee.jpg\"]")
        List<String> coupleCourseImageObjectKeyList,

        @Schema(description = "핀 이미지 objectKey 리스트")
        List<PinImageObjectKeyDto> pinImageObjectKeyList
) {
}
