package com.meetup.hereandnow.auth.application;

import com.meetup.hereandnow.auth.infrastructure.KakaoUserInfo;
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
        Map<String, Object> attributes = oAuth2User.getAttributes();

        System.out.println(attributes.toString());

        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(attributes);

        Member member = memberRepository.findByEmail(kakaoUserInfo.getEmail())
                .orElseGet(() -> {
                    Member newMember = MemberMapper.toEntity(kakaoUserInfo);
                    return memberRepository.save(newMember);
                });

        return new CustomUserDetails(member, attributes);
    }

    private KakaoUserInfo getKakaoUserInfo(Map<String, Object> attributes) {
        return new KakaoUserInfo(attributes);
    }
}
