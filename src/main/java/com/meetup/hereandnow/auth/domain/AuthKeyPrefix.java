package com.meetup.hereandnow.auth.domain;

import lombok.Getter;

@Getter
public enum AuthKeyPrefix {
    AUTH_TOKEN("auth:"),
    REFRESH_TOKEN("refreshToken:");

    private final String prefix;

    AuthKeyPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String key(String suffix) {
        return prefix + suffix;
    }
}
