package com.meetup.hereandnow.core.infrastructure.objectstorage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value = "ncp")
public record ObjectStorageProperties(
        String access,
        String secret,
        String region,
        String endpoint,
        String bucketName
) {
}
