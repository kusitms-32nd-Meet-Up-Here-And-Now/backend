package com.meetup.hereandnow.auth.application.oauth;

import com.meetup.hereandnow.core.infrastructure.security.CustomUserDetails;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.domain.value.Provider;
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

    @BeforeEach
    void setUp() {
        oAuth2User = mock(OAuth2User.class);
    }

    @DisplayName("신규 카카오 사용자가 로그인하면 회원가입 처리 후 유저 정보를 반환한다")
    @Test
    void processNewKakaoUser() {
        // given
        Map<String, Object> kakaoAttributes = new HashMap<>();
        Map<String, Object> kakaoAccount = new HashMap<>();
        Map<String, Object> profile = new HashMap<>();
        profile.put("nickname", "testuser");
        profile.put("profile_image_url", "test_image.jpg");
        kakaoAccount.put("email", "test@example.com");
        kakaoAccount.put("profile", profile);
        kakaoAttributes.put("kakao_account", kakaoAccount);
        kakaoAttributes.put("id", 123456789L);

        given(oAuth2User.getAttributes()).willReturn(kakaoAttributes);
        given(memberRepository.findByEmailAndProvider("test@example.com", Provider.KAKAO)).willReturn(Optional.empty());

        Member newMember = Member.builder()
                .email("test@example.com")
                .nickname("testuser")
                .profileImage("test_image.jpg")
                .provider(Provider.KAKAO)
                .build();
        given(memberRepository.save(any(Member.class))).willReturn(newMember);

        // when
        OAuth2User result = customOAuth2UserService.processOAuth2User(oAuth2User, "kakao");

        // then
        assertThat(result).isInstanceOf(CustomUserDetails.class);
        CustomUserDetails userDetails = (CustomUserDetails) result;
        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails.member().getNickname()).isEqualTo("testuser");
        verify(memberRepository).save(any(Member.class));
    }

    @DisplayName("기존 카카오 사용자가 로그인하면 유저 정보를 반환한다")
    @Test
    void processExistingKakaoUser() {
        // given
        Map<String, Object> kakaoAttributes = new HashMap<>();
        Map<String, Object> kakaoAccount = new HashMap<>();
        Map<String, Object> profile = new HashMap<>();
        profile.put("nickname", "testuser");
        profile.put("profile_image_url", "test_image.jpg");
        kakaoAccount.put("email", "test@example.com");
        kakaoAccount.put("profile", profile);
        kakaoAttributes.put("kakao_account", kakaoAccount);
        kakaoAttributes.put("id", 123456789L);

        given(oAuth2User.getAttributes()).willReturn(kakaoAttributes);
        Member existingMember = Member.builder()
                .id(1L)
                .email("test@example.com")
                .nickname("existingUser")
                .profileImage("existing_image.jpg")
                .provider(Provider.KAKAO)
                .build();
        given(memberRepository.findByEmailAndProvider("test@example.com", Provider.KAKAO)).willReturn(Optional.of(existingMember));

        // when
        OAuth2User result = customOAuth2UserService.processOAuth2User(oAuth2User, "kakao");

        // then
        assertThat(result).isInstanceOf(CustomUserDetails.class);
        CustomUserDetails userDetails = (CustomUserDetails) result;
        assertThat(userDetails.getUsername()).isEqualTo("test@example.com");
        assertThat(userDetails.member().getNickname()).isEqualTo("existingUser");
        assertThat(userDetails.member().getId()).isEqualTo(1L);
        verify(memberRepository, never()).save(any(Member.class));
    }

    @DisplayName("신규 구글 사용자가 로그인하면 회원가입 처리 후 유저 정보를 반환한다")
    @Test
    void processNewGoogleUser() {
        // given
        Map<String, Object> googleAttributes = new HashMap<>();
        googleAttributes.put("sub", "123456789");
        googleAttributes.put("name", "googleuser");
        googleAttributes.put("email", "google@example.com");
        googleAttributes.put("picture", "google_image.jpg");

        given(oAuth2User.getAttributes()).willReturn(googleAttributes);
        given(memberRepository.findByEmailAndProvider("google@example.com", Provider.GOOGLE)).willReturn(Optional.empty());

        Member newMember = Member.builder()
                .email("google@example.com")
                .nickname("googleuser")
                .profileImage("google_image.jpg")
                .provider(Provider.GOOGLE)
                .build();
        given(memberRepository.save(any(Member.class))).willReturn(newMember);

        // when
        OAuth2User result = customOAuth2UserService.processOAuth2User(oAuth2User, "google");

        // then
        assertThat(result).isInstanceOf(CustomUserDetails.class);
        CustomUserDetails userDetails = (CustomUserDetails) result;
        assertThat(userDetails.getUsername()).isEqualTo("google@example.com");
        assertThat(userDetails.member().getNickname()).isEqualTo("googleuser");
        verify(memberRepository).save(any(Member.class));
    }

    @DisplayName("기존 구글 사용자가 로그인하면 유저 정보를 반환한다")
    @Test
    void processExistingGoogleUser() {
        // given
        Map<String, Object> googleAttributes = new HashMap<>();
        googleAttributes.put("sub", "123456789");
        googleAttributes.put("name", "googleuser");
        googleAttributes.put("email", "google@example.com");
        googleAttributes.put("picture", "google_image.jpg");

        given(oAuth2User.getAttributes()).willReturn(googleAttributes);
        Member existingMember = Member.builder()
                .id(2L)
                .email("google@example.com")
                .nickname("existingGoogleUser")
                .profileImage("existing_google_image.jpg")
                .provider(Provider.GOOGLE)
                .build();
        given(memberRepository.findByEmailAndProvider("google@example.com", Provider.GOOGLE)).willReturn(Optional.of(existingMember));

        // when
        OAuth2User result = customOAuth2UserService.processOAuth2User(oAuth2User, "google");

        // then
        assertThat(result).isInstanceOf(CustomUserDetails.class);
        CustomUserDetails userDetails = (CustomUserDetails) result;
        assertThat(userDetails.getUsername()).isEqualTo("google@example.com");
        assertThat(userDetails.member().getNickname()).isEqualTo("existingGoogleUser");
        assertThat(userDetails.member().getId()).isEqualTo(2L);
        verify(memberRepository, never()).save(any(Member.class));
    }
}
