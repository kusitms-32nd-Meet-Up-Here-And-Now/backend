package com.meetup.hereandnow.auth.domain;

public interface OAuth2UserInfo {

    String getProvider();
    String getProviderId();
    String getName();
    String getEmail();
    String getProfileImage();
}
