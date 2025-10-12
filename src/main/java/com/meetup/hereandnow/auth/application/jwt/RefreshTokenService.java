package com.meetup.hereandnow.auth.application.jwt;

import com.meetup.hereandnow.auth.domain.AuthKeyPrefix;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "refreshToken:";

    // RefreshToken 저장
    public void saveToken(Long memberId, String refreshToken, Duration duration) {
        redisTemplate.opsForValue().set(getRefreshTokenKey(memberId), refreshToken, duration);
    }

    // RefreshToken 조회
    public String getToken(Long memberId) {
        return redisTemplate.opsForValue().get(getRefreshTokenKey(memberId));
    }

    // RefreshToken 삭제
    public void deleteToken(Long memberId) {
        redisTemplate.delete(getRefreshTokenKey(memberId));
    }

    private String getRefreshTokenKey(Long memberId){
        return AuthKeyPrefix.REFRESH_TOKEN.key(String.valueOf(memberId));
    }
}
