package com.meetup.hereandnow.integration.fixture.member;

import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.domain.value.Provider;
import java.util.ArrayList;
import java.util.List;

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

    public static Member getMember(String email) {
        return Member.builder()
                .email(email)
                .nickname(email)
                .profileImage(PROFILE_IMAGE)
                .providerId(email)
                .provider(Provider.KAKAO)
                .username(email)
                .build();
    }

    public static Member getMember(int index) {

        return Member.builder()
                .email("test" + index + "@test.com")
                .nickname("test" + index + "_member")
                .profileImage(PROFILE_IMAGE)
                .providerId(index + "")
                .provider(getProvider(index))
                .username("test" + index + "_username")
                .build();
    }

    public static List<Member> getMembers(int size) {
        List<Member> memberList = new ArrayList<>();

        if (size < 0) {
            return List.of(getMember());
        }

        for (int i = 0; i < size; i++) {
            Member member = getMember(i);

            memberList.add(member);
        }

        return memberList;
    }

    private static Provider getProvider(int index) {
        return (index % 2) == 0 ? Provider.KAKAO : Provider.GOOGLE;
    }

}
