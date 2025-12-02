package com.meetup.hereandnow.member.infrastructure.mapper;

import com.meetup.hereandnow.auth.domain.OAuth2UserInfo;
import com.meetup.hereandnow.member.domain.Member;

public class MemberMapper {

    private MemberMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static Member toEntity(OAuth2UserInfo oAuth2UserInfo) {
        return Member.builder()
                .email(oAuth2UserInfo.getEmail())
                .nickname(oAuth2UserInfo.getName())
                .profileImage(oAuth2UserInfo.getProfileImage())
                .providerId(oAuth2UserInfo.getProviderId())
                .provider(oAuth2UserInfo.getProvider())
                .username(splitUsername(oAuth2UserInfo.getEmail()))
                .build();
    }

    private static String splitUsername(String email) {
        String[] splitByEmail = email.split("@");

        return splitByEmail[0];
    }
}
