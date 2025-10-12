package com.meetup.hereandnow.auth.infrastructure;

import com.meetup.hereandnow.auth.domain.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class KakaoUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getName() {
        return (String) getKakaoAccount().get("name");
    }

    @Override
    public String getEmail() {
        return (String) getKakaoAccount().get("email");
    }

    @Override
    public String getProfileImage() {
        return (String) getKakaoProfile().get("profile_image_url");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getKakaoAccount() {
        Object account = attributes.get("kakao_account");
        if (account instanceof Map) {
            return (Map<String, Object>) account;
        }
        return Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getKakaoProfile() {
        Object profile = attributes.get("profile");
        if (profile instanceof Map) {
            return (Map<String, Object>) profile;
        }
        return Collections.emptyMap();
    }
}
