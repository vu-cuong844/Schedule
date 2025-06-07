package com.example.gateway.util;


import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private static final String AUTHORITIES_KEY = "roles";
    private static final String TEACHER_CODE = "teacher_code";

    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims getAllClaimsFromToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Failed to parse JWT claims: {}", e.getMessage());
            return null;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (claims != null) ? claims.getSubject() : null;
    }

    public String getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (claims != null) ? claims.get(AUTHORITIES_KEY, String.class) : null;
    }

    public String getTeacherCodeFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (claims != null) ? claims.get(TEACHER_CODE, String.class) : null;
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            logger.warn("JWT token string is empty or null.");
            return false;
        }
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token format: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (JwtException | IllegalArgumentException ex) {
            logger.error("JWT validation error: {}", ex.getMessage());
        }
        return false;
    }
}
