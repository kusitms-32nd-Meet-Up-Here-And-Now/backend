package com.meetup.hereandnow.auth.infrastructure.oauth;

import com.meetup.hereandnow.auth.domain.OAuth2UserInfo;
import com.meetup.hereandnow.member.domain.value.Provider;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class GoogleUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;

    @Override
    public Provider getProvider() {
        return Provider.GOOGLE;
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getProfileImage() {
        return (String) attributes.get("picture");
    }
}
