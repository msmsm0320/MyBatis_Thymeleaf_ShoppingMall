package com.example.shoppingmall.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-exp}")
    private long accessExpirationTime; // AccessToken 만료시간

    @Value("${jwt.refresh-exp}")
    private long refreshExpirationTime; // RefreshToken 만료시간

    // JWT 서명용 키 생성
    private Key key(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // AccessToken 생성
    public String createAccess(Long userId, String email, List<String> roles, long refreshVersion){
        Date now = new Date(); // 현재 시간 설정
        Date exp = new Date(now.getTime() + accessExpirationTime); // 만료 시간(현재 시간 + accessExpirationTime) 설정

        return Jwts.builder()
                .setSubject(String.valueOf(userId)) // 사용자 ID (문자열)
                .claim("email", email) // Payload에 담길 Claim 값 설정 -> 이메일
                .claim("roles", roles) // 권한 정보
                .claim("rv", refreshVersion) // refreshToken Version
                .setIssuedAt(now) // 토큰 생성 시간을 설정 -> 발급 시간
                .setExpiration(exp) // 토큰 만료 시간을 설정 -> 만료 시간
                .signWith(key(), SignatureAlgorithm.HS256) // 어던 알고리즘 및 키값으로 서명할지 설정 -> key(), HS256 서명
                .compact(); // 토큰 생성
    }

    // RefreshToken 생성
    public String createRefresh(Long userId, String email, List<String> roles, long refreshVersion){
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshExpirationTime);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("rv", refreshVersion)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT 토큰을 해석, 서명 검증, 만료시간 확인 후 -> 내부 Claims들 반환
    public Jws<Claims> parse(String token){
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token);
    }

    /* Claims에서

    각각 userId, Email, Roles, RefreshVersion

    을 가져오는 메서드*/
    public Long getUserId(Claims claims){
        return Long.valueOf(claims.getSubject());
    }

    public String getEmail(Claims claims){
        return claims.get("email", String.class);
    }

    public List<String> getRoles(Claims claims){
        return claims.get("roles", List.class);
    }

    public long getRefreshVersion(Claims claims){
        return claims.get("rv", Long.class);
    }
}
