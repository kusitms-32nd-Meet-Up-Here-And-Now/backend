package com.meetup.hereandnow.auth.application.oauth;

import com.meetup.hereandnow.core.infrastructure.security.CustomUserDetails;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    @Mock
    private MemberRepository memberRepository;

    private OAuth2User oAuth2User;
    private Map<String, Object> attributes;
    private Map<String, Object> kakaoAccount;
    private Map<String, Object> profile;

    @BeforeEach
    void setUp() {
        oAuth2User = mock(OAuth2User.class);
        attributes = new HashMap<>();
        kakaoAccount = new HashMap<>();
        profile = new HashMap<>();

        profile.put("nickname", "testuser");
        profile.put("profile_image_url", "test_image.jpg");
        kakaoAccount.put("email", "test@example.com");
        kakaoAccount.put("profile", profile);
        attributes.put("kakao_account", kakaoAccount);
        attributes.put("id", 123456789L);
    }

    @DisplayName("신규 사용자가 로그인하면 회원가입 처리 후 유저 정보를 반환한다")
    @Test
    void processNewUser() {
        // given
        given(oAuth2User.getAttributes()).willReturn(attributes);
        given(memberRepository.findByEmail("test@example.com")).willReturn(Optional.empty());

        Member newMember = Member.builder()
                .email("test@example.com")
                .nickname("testuser")
                .profileImage("test_image.jpg")
                .build();
        given(memberRepository.save(any(Member.class))).willReturn(newMember);

        // when
        OAuth2User result = customOAuth2UserService.processOAuth2User(oAuth2User);

        // then
        assertThat(result).isInstanceOf(CustomUserDetails.class);
        CustomUserDetails userDetails = (CustomUserDetails) result;
        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails.member().getNickname()).isEqualTo("testuser");
        verify(memberRepository).save(any(Member.class));
    }

    @DisplayName("기존 사용자가 로그인하면 유저 정보를 반환한다")
    @Test
    void processExistingUser() {
        // given
        given(oAuth2User.getAttributes()).willReturn(attributes);
        Member existingMember = Member.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("existingUser")
                .profileImage("existing_image.jpg")
                .build();
        given(memberRepository.findByEmail("test@example.com")).willReturn(Optional.of(existingMember));

        // when
        OAuth2User result = customOAuth2UserService.processOAuth2User(oAuth2User);

        // then
        assertThat(result).isInstanceOf(CustomUserDetails.class);
        CustomUserDetails userDetails = (CustomUserDetails) result;
        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails.member().getNickname()).isEqualTo("existingUser");
        assertThat(userDetails.member().getId()).isEqualTo(1L);
        verify(memberRepository, never()).save(any(Member.class));
    }
}
