package com.meetup.hereandnow.auth.application.oauth;

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
        return processOAuth2User(oAuth2User);
    }

    OAuth2User processOAuth2User(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(attributes);

        Member member = memberRepository.findByEmail(kakaoUserInfo.getEmail())
                .orElseGet(() -> memberRepository.save(MemberMapper.toEntity(kakaoUserInfo)));

        return new CustomUserDetails(member, attributes);
    }


    private KakaoUserInfo getKakaoUserInfo(Map<String, Object> attributes) {
        return new KakaoUserInfo(attributes);
    }
}