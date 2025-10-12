package com.meetup.hereandnow.member.infrastructure.mapper;

import com.meetup.hereandnow.auth.domain.OAuth2UserInfo;
import com.meetup.hereandnow.member.domain.Member;
import org.springframework.stereotype.Component;

@Component
public class MemberMapper {

    public static Member toEntity(OAuth2UserInfo oAuth2UserInfo) {
        return Member.builder()
                .email(oAuth2UserInfo.getEmail())
                .nickname(oAuth2UserInfo.getName())
                .profileImage(oAuth2UserInfo.getProfileImage())
                .providerId(oAuth2UserInfo.getProviderId())
                .build();
    }
}
