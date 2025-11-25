package com.meetup.hereandnow.integration.fixture.member;

import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.domain.value.Provider;

public class MemberEntityFixture {

    private static final String PROFILE_IMAGE = "http://img1.kakaocdn.net/thumb/R640x640.q70/?fname=http://t1.kakaocdn.net/account_images/default_profile.jpeg";

    public static Member getMember() {
        return Member.builder()
                .email("test@test.com")
                .nickname("test_member")
                .profileImage(PROFILE_IMAGE)
                .providerId("123456789")
                .provider(Provider.KAKAO)
                .username("test_username")
                .build();
    }
}
