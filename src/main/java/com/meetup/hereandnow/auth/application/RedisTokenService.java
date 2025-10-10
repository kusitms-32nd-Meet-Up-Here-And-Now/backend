package com.meetup.hereandnow.auth.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    // RefreshToken 저장
    public void saveToken(String memberId, String refreshToken, Duration duration) {
        redisTemplate.opsForValue().set(memberId, refreshToken, duration);
    }

    // RefreshToken 조회
    public String getToken(String memberId) {
        return redisTemplate.opsForValue().get(memberId);
    }

    // RefreshToken 삭제
    public void deleteToken(String memberId) {
        redisTemplate.delete(memberId);
    }
}
