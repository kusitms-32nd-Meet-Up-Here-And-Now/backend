package com.meetup.hereandnow.course.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CourseCardResponseDto(

        @Schema(description = "코스 ID", example = "1")
        Long courseId,

        @Schema(description = "사용자 프로필 이미지", example = "https://...")
        String memberProfileImage,

        @Schema(description = "사용자 닉네임 (마스킹됨)", example = "홍**")
        String memberNickname,

        @Schema(description = "코스 제목", example = "아직 어색한 사이인 커플을 위한 감성 충만 코스")
        String courseTitle,

        @Schema(description = "코스 지역", example = "강남")
        String courseRegion,

        @Schema(description = "코스 내 장소 수", example = "5")
        int pinCount,

        @Schema(description = "코스 태그 리스트", example = "[\"음식이 맛있어요\", \"사진 찍기 좋아요\"]")
        List<String> courseTags,

        @Schema(description = "코스 이미지 리스트", example = "[\"https://...\", \"https://...\"]")
        List<String> courseImages
) {
}
