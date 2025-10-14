package com.meetup.hereandnow.core.infrastructure.security;

import com.meetup.hereandnow.member.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public record CustomUserDetails(Member member, Map<String, Object> attributes) implements UserDetails, OAuth2User {

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public String getName() {
        return member.getProviderId();
    }
}
