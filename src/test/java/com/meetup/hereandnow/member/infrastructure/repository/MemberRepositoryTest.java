package com.meetup.hereandnow.member.infrastructure.repository;

import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.domain.value.Provider;
import com.meetup.hereandnow.support.RepositoryTestSupport;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.*;

class MemberRepositoryTest extends RepositoryTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    private static final Long MEMBER_ID = 1L;
    private static final String USERNAME = "username";
    private static final String NICKNAME = "nickname";
    private static final Provider PROVIDER = Provider.KAKAO;
    private static final String EMAIL = "test01@test.com";

    private Member mockMember;

    @BeforeEach
    void setUp() {
        mockMember = Member.builder()
                .id(MEMBER_ID)
                .email(EMAIL)
                .nickname(NICKNAME)
                .provider(PROVIDER)
                .providerId("10293948")
                .username(USERNAME)
                .build();
    }

    @Test
    @DisplayName("이메일 + Provider로 회원 조회에 성공한다.")
    void success_find_by_email_and_provider() {
        // given
        memberRepository.save(mockMember);

        // when
        Optional<Member> findMember = memberRepository.findByEmailAndProvider(EMAIL, PROVIDER);

        // then
        assertThat(findMember).isPresent();
        Member optionalMember = findMember.get();
        assertThat(optionalMember.getEmail()).isEqualTo("test01@test.com");
        assertThat(optionalMember.getProvider()).isEqualTo(Provider.KAKAO);
    }

    @Test
    @DisplayName("username으로 회원 조회에 성공한다.")
    void success_find_by_username() {
        // given
        memberRepository.save(mockMember);

        // when
        Optional<Member> findMember = memberRepository.findByUsername(USERNAME);

        // then
        assertThat(findMember).isPresent();
        Member optionalMember = findMember.get();
        assertThat(optionalMember.getUsername()).isEqualTo("username");
    }
}