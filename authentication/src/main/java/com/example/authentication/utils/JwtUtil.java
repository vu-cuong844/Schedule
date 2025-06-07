package com.example.authentication.utils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.authentication.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORITIES_KEY = "roles";
    private static final String TEACHER_CODE = "teacher_code";

    private final SecretKey key;
    private final Long expiration;
    private final Long refreshExpiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") Long expiration,
            @Value("${jwt.refreshExpiration}") Long refreshExpiration) {

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expiration = expiration;
        this.refreshExpiration = refreshExpiration;
    }

    // tạo accesTOken
    public String generateToken(String username, String role, String teacherCode) {
        return Jwts.builder()
                .subject(username)
                .claim(AUTHORITIES_KEY, role) // Sử dụng AUTHORITIES_KEY = "roles" thay vì "role"
                .claim(TEACHER_CODE, teacherCode)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.key, Jwts.SIG.HS512)
                .compact();
    }

    // tạo refreshToken
    public String generateRefreshToken(String username, String role, String teacherCode) {
        return Jwts.builder()
                .subject(username)
                .claim(AUTHORITIES_KEY, role)
                .claim(TEACHER_CODE, teacherCode)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(this.key, Jwts.SIG.HS512)
                .compact();
    }

    // extract token từ reuqest header
    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        System.out.println(bearerToken + "\n \n \n");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        logger.trace("No JWT token found in request headers");
        return null;
    }

    // lấy trên đang nhập từ token
    public String getUsernameFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (claims != null) ? claims.getSubject() : null;
    }

    // lấy roles từ token
    public String getRolesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (claims != null) ? claims.get(AUTHORITIES_KEY, String.class) : null;
    }

    public String getTeacgerCodeFrom(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return (claims != null) ? claims.get(TEACHER_CODE, String.class) : null;
    }

    // lấy thông tin từ token
    private Claims getAllClaimsFromToken(String token) {
        if (!StringUtils.hasText(token)) {
            return null;
        }
        try {
            return Jwts.parser()
                    .verifyWith(this.key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            logger.trace("Failed to parse JWT claims: {}", e.getMessage());
            return null;
        }
    }

    // kiểm tra hợp lêk
    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            logger.warn("JWT token string is empty or null.");
            return false;
        }
        try {
            Jwts.parser()
                    .verifyWith(this.key)
                    .build()
                    .parseSignedClaims(token);

            logger.trace("JWT token is valid using new API");
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token format: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.warn("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT token compact of handler are invalid: {}", ex.getMessage());
        } catch (JwtException ex) {
            logger.error("General JWT validation error: {}", ex.getMessage());
        }
        return false;
    }

    // kiểm tra hạn
    public boolean validateToken(String token, User user) {
        if (!validateToken(token)) {
            return false;
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(this.key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Date issueAt = claims.getIssuedAt();
            LocalDateTime passwordchangedAt = user.getPasswordChangedAt();

            if (passwordchangedAt != null) {
                Instant tokenIssueAt = issueAt.toInstant();
                Instant passwordChangedInstant = passwordchangedAt.atZone(ZoneId.systemDefault()).toInstant();
                if (tokenIssueAt.isBefore(passwordChangedInstant)) {
                    logger.warn("Token issued before last password change.");
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("Failed to validate token with password change time: {}", e.getMessage());
            return false;
        }
    }

    

}