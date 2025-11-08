package com.meetup.hereandnow.connect.dto.response;

import com.meetup.hereandnow.connect.domain.Couple;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record CoupleInfoResponseDto(
        @Schema(description = "지금까지 만난 날(만난 날 설정 안한 경우 0)", example = "999")
        long datingDate,

        @Schema(description = "멤버 1 이름 (nickname, 왼쪽)", example = "김히어")
        String member1Name,

        @Schema(description = "멤버 2 이름 (nickname, 오른쪽)", example = "이나우")
        String member2Name,

        @Schema(description = "멤버 1 프로필 이미지(imageUrl, 왼쪽)", example = "http://~~~")
        String member1ImageUrl,

        @Schema(description = "멤버 2 프로필 이미지(imageUrl, 왼쪽)", example = "http://~~~")
        String member2ImageUrl,

        @Schema(description = "우리가 함께한 장소 개수", example = "12")
        int placeWithCount,

        @Schema(description = "우리가 함께한 코스 개수", example = "3")
        int courseWithCount
) {

    public static CoupleInfoResponseDto from(
            LocalDate startDate, Couple couple, int placeWithCount, int courseWithCount
    ) {
        return new CoupleInfoResponseDto(
                ChronoUnit.DAYS.between(startDate, LocalDate.now()),
                couple.getMember1().getNickname(),
                couple.getMember2().getNickname(),
                couple.getMember1().getProfileImage(),
                couple.getMember2().getProfileImage(),
                placeWithCount,
                courseWithCount
        );
    }
}
