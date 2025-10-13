package com.meetup.hereandnow.auth.application.oauth;

import com.meetup.hereandnow.auth.domain.OAuth2UserInfo;
import com.meetup.hereandnow.auth.infrastructure.oauth.GoogleUserInfo;
import com.meetup.hereandnow.auth.infrastructure.oauth.KakaoUserInfo;
import com.meetup.hereandnow.core.infrastructure.security.CustomUserDetails;
import com.meetup.hereandnow.member.domain.Member;
import com.meetup.hereandnow.member.infrastructure.mapper.MemberMapper;
import com.meetup.hereandnow.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);
        String registrationId = request.getClientRegistration().getRegistrationId();

        return processOAuth2User(oAuth2User, registrationId);
    }

    OAuth2User processOAuth2User(OAuth2User oAuth2User, String registrationId) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2UserInfo userInfo = getOAuth2UserInfo(attributes, registrationId);

        Member member = memberRepository.findByEmailAndProvider(userInfo.getEmail(), userInfo.getProvider())
                .orElseGet(() -> memberRepository.save(MemberMapper.toEntity(userInfo)));

        return new CustomUserDetails(member, attributes);
    }

    private OAuth2UserInfo getOAuth2UserInfo(Map<String, Object> attributes, String registrationId) {
        return switch (registrationId) {
            case "kakao" -> new KakaoUserInfo(attributes);
            case "google" -> new GoogleUserInfo(attributes);
            default -> throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        };
    }
}