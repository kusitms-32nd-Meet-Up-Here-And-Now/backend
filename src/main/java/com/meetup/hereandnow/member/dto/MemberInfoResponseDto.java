package com.meetup.hereandnow.member.dto;

import com.meetup.hereandnow.connect.domain.Couple;
import com.meetup.hereandnow.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberInfoResponseDto(

        @Schema(example = "hereandnow")
        String username,

        @Schema(example = "hereandnow@gmail.com")
        String email,

        @Schema(example = "김히어")
        String nickname,

        @Schema(example = "http://~~")
        String profileImageUrl,

        @Schema(example = "true")
        boolean isCouple
) {
    public static MemberInfoResponseDto from(Member member, Couple couple) {
        return new MemberInfoResponseDto(
                member.getUsername(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImage(),
                couple != null ? true : false
        );
    }
}
