package com.meetup.hereandnow.auth.infrastructure;

import com.meetup.hereandnow.auth.exception.JwtErrorCode;
import com.meetup.hereandnow.member.domain.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class TokenProvider {

    private final Key key;
    private final JwtProperties jwtProperties;

    public TokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // Access 토큰 생성
    public String createAccessToken(Member member) {
        long now = (new Date()).getTime();
        Date expiresIn = new Date(now + jwtProperties.accessExp() * 1000L);
        return Jwts.builder()
                .subject(member.getId().toString())
                .claim("type", "Access")
                .expiration(expiresIn)
                .signWith(key)
                .compact();
    }

    // Refresh 토큰 생성
    public String createRefreshToken() {
        long now = (new Date()).getTime();
        Date expiresIn = new Date(now + jwtProperties.refreshExp() * 1000L);
        return Jwts.builder()
                .claim("type", "Refresh")
                .expiration(expiresIn)
                .signWith(key)
                .compact();
    }

    // 토큰 검증
    public boolean isValid(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw JwtErrorCode.TOKEN_EXPIRED.toException();
        } catch (SecurityException e) {
            throw JwtErrorCode.TOKEN_SIG_INVALID.toException();
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            throw JwtErrorCode.TOKEN_UNSUPPORTED.toException();
        } catch (Exception e) {
            throw JwtErrorCode.TOKEN_INVALID.toException();
        }
    }

    // 토큰에서 claim 값 추출
    public Claims resolveTokenClaims(String token) {
        return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token).getPayload();
    }
}
