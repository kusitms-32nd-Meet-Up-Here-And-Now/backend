package com.meetup.hereandnow.core.infrastructure.objectstorage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;

import java.net.URI;
import java.net.URISyntaxException;

@Service
@RequiredArgsConstructor
public class ObjectStorageService {

    private final S3Client storageClient;
    private final ObjectStorageProperties properties;

    public void delete(String key) {
        DeleteObjectRequest req = DeleteObjectRequest.builder()
                .bucket(properties.bucketName())
                .key(isUrl(key) ? extractKey(key) : key)
                .build();
        storageClient.deleteObject(req);
    }

    public boolean exists(String key) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                    .bucket(properties.bucketName())
                    .key(isUrl(key) ? extractKey(key) : key)
                    .build();
            storageClient.headObject(request);
            return true;
        } catch (AwsServiceException e) {
            if (e.statusCode() == HttpStatus.NOT_FOUND.value()) {
                return false;
            }
            handleAwsError(e);
        }
        return false;
    }

    private void handleAwsError(AwsServiceException e) {
        int code = e.statusCode();
        if (code == HttpStatus.FORBIDDEN.value()) {
            throw ObjectStorageErrorCode.ACCESS_DENIED.toException();
        } else if (code == HttpStatus.BAD_REQUEST.value()) {
            throw ObjectStorageErrorCode.BAD_REQUEST.toException();
        } else if (code == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            throw ObjectStorageErrorCode.AWS_SERVER_ERROR.toException();
        }
    }

    // 전체 https 이미지 주소가 주어졌을 때 key만 추출
    private String extractKey(String imageUrl) {
        try {
            String path = new URI(imageUrl).getPath();
            return path.substring(path.indexOf('/', 1) + 1);
        } catch (URISyntaxException e) {
            throw ObjectStorageErrorCode.OBJECT_URI_ERROR.toException();
        }
    }

    private boolean isUrl(String imageUrl) {
        return imageUrl.startsWith("https://") || imageUrl.startsWith("http://");
    }
}
