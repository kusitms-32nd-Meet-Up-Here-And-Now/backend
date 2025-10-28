package com.meetup.hereandnow.course.dto;

import com.meetup.hereandnow.course.domain.value.CourseTagEnum;
import com.meetup.hereandnow.course.dto.request.CoupleCourseRecordSaveRequestDto;
import com.meetup.hereandnow.pin.dto.PinSaveDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record CourseSaveDto(

        @Schema(description = "코스 제목", example = "홍대 데이트 코스 완벽 정리")
        String courseTitle,

        @Schema(description = "코스 평점", example = "4")
        double courseRating,

        @Schema(description = "코스 설명", example = "홍대 놀거리 리스트")
        String courseDescription,

        @Schema(description = "코스 공개 여부", example = "true")
        Boolean isPublic,

        @Schema(description = "코스 태그 리스트", example = "[\"COZY\",\"EXCITED\"]")
        List<CourseTagEnum> courseTagList,

        CoupleCourseRecordSaveRequestDto coupleCourseRecordSaveRequestDto,

        List<PinSaveDto> pinList
) {

}
