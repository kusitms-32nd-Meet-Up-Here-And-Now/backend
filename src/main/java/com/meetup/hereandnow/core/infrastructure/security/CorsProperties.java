package com.meetup.hereandnow.core.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "cors.allow")
public record CorsProperties(
        String paths,
        List<String> origins,
        List<String> methods,
        List<String> headers,
        List<String> exposedHeaders,
        Long maxAge,
        Boolean credentials
) {
}
