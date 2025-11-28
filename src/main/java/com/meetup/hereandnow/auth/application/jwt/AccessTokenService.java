package com.meetup.hereandnow.auth.application.jwt;

import com.meetup.hereandnow.auth.domain.AuthKeyPrefix;
import com.meetup.hereandnow.auth.infrastructure.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AccessTokenService {

    private final StringRedisTemplate redisTemplate;
    private final JwtProperties jwtProperties;

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

    private String getAuthTokenKey(String authKey) {
        return AuthKeyPrefix.AUTH_TOKEN.key(authKey);
    }
}
