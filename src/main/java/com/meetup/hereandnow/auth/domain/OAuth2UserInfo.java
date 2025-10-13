package com.meetup.hereandnow.auth.domain;

public interface OAuth2UserInfo {

    String getProviderId();
    String getName();
    String getEmail();
    String getProfileImage();
}
