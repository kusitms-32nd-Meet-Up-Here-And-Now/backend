package com.meetup.hereandnow.core.config.ncp;

import com.meetup.hereandnow.core.infrastructure.objectstorage.ObjectStorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class ObjectStorageConfiguration {

    private final ObjectStorageProperties properties;

    @Bean(destroyMethod = "close")
    public S3Client storageClient() {
        return S3Client.builder()
                .endpointOverride(URI.create(properties.endpoint()))
                .region(Region.of(properties.region()))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(properties.access(), properties.secret())))
                .build();
    }
}
