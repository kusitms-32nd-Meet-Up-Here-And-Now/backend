package com.meetup.hereandnow.archive.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CourseCardDto(

        @Schema(description = "코스 ID", example = "1")
        Long id,

        @Schema(description = "코스 제목", example = "홍대 데이트 코스 완벽 정리")
        String courseTitle,

        @Schema(description = "코스 설명", example = "홍대 놀거리 리스트")
        String courseDescription,

        @Schema(description = "코스 태그 리스트", example = "[\"뷰가 좋아요\",\"사진이 잘 나와요\"]")
        List<String> courseTagList,

        @Schema(description = "코스 조회수", example = "2501")
        Integer viewCount,

        @Schema(description = "코스 평점", example = "4")
        double courseRating,

        @Schema(description = "코스 사진", example = "[\"/course/8b18/pins/92eb/images/a558.jpg\",\"/course/8b18/pins/t3gs/images/a5eh.jpg\"]")
        List<String> imageUrl
) {
}
