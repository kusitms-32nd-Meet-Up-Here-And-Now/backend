package com.meetup.hereandnow.auth.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        int accessExp,
        int refreshExp
) {
}
