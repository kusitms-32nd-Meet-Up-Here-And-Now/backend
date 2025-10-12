package com.meetup.hereandnow.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AccessTokenService {

    private final StringRedisTemplate redisTemplate;
    private final String AUTH_TOKEN_PREFIX = "auth:";

    public void saveToken(
            String authKey,
            String accessToken,
            Duration duration
    ) {
        redisTemplate.opsForValue().set(
                getAuthTokenKey(authKey),
                accessToken,
                Duration.ofSeconds(duration.getSeconds())
        );
    }

    public String getToken(String authKey) {
        return redisTemplate.opsForValue().get(getAuthTokenKey(authKey));
    }

    public void deleteToken(String authKey) {
        redisTemplate.delete(getAuthTokenKey(authKey));
    }

    private String getAuthTokenKey(String tempAuthKey) {
        return AUTH_TOKEN_PREFIX + tempAuthKey;
    }
}
