package com.meetup.hereandnow.place.dto;

import com.meetup.hereandnow.course.dto.response.CourseCardResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record PlaceInfoResponseDto(
        
        @Schema(description = "장소 상세")
        PlaceCardResponseDto placeCardResponseDto,

        @Schema(description = "장소 태그 리스트", example = "[\"사진 찍기 좋아요\",\"분위기가 좋아요\"]")
        List<String> placeTagList,

        @Schema(description = "위쪽 배너 이미지", example = "[\"http://~\", \"http://~\"]")
        List<String> bannerImageList,

        @Schema(description = "지도 하단 이미지", example = "[\"http://~\", \"http://~\"]")
        List<String> placeInfoImageList,

        @Schema(description = "장소 좋았던 점 리스트", example = "[\"입장료가 무료라서 좋고 내부도 이뻐요\",\"음식이 맛있어요\"]")
        List<String> placePositiveList,

        @Schema(description = "장소 아쉬운 점 리스트", example = "[\"불친절해요\",\"깨끗하지 않아요\"]")
        List<String> placeNegativeList,

        @Schema(description = "연관 코스 리스트")
        List<CourseCardResponseDto> courseList
) {
}
