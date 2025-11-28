package com.meetup.hereandnow.auth.domain;

import com.meetup.hereandnow.member.domain.value.Provider;

public interface OAuth2UserInfo {

    Provider getProvider();
    String getProviderId();
    String getName();
    String getEmail();
    String getProfileImage();
}
