package com.example.authentication.config;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.authentication.model.User;
import com.example.authentication.service.UserService;
import com.example.authentication.utils.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String ROLE_PREFIX = "ROLE_";
    private static final List<String> WHITELIST = List.of(
            "/api/auth/login",
            // "/api/auth/register",
            "/api/auth/refresh-token",
            "/api/auth/reset-password",
            "/api/auth/forgot-password");

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        if (WHITELIST.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Kiểm tra nếu đã có authentication
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            logger.trace("SecurityContext already holds authentication '{}', skipping JWT filter.",
                    SecurityContextHolder.getContext().getAuthentication().getName());
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = jwtUtil.extractTokenFromRequest(request);

            if (jwt != null) {
                logger.debug("Processing JWT token for request to: {}", request.getRequestURI());

                if (jwtUtil.validateToken(jwt)) {
                    String username = jwtUtil.getUsernameFromToken(jwt);
                    String role = jwtUtil.getRolesFromToken(jwt);
                    String cleanRole = role
                            .replace("[", "")
                            .replace("]", "")
                            .trim();

                    logger.debug("Cleaned role string: {}", cleanRole);
                    User user = userService.getUserByUsername(username);

                    if (user != null) {
                        if (jwtUtil.validateToken(jwt, user)) {
                            logger.debug("Extracted from token - Username: {}, Role: {}", username, role);

                            if (StringUtils.hasText(username) && StringUtils.hasText(role)) {
                                String authorityString = cleanRole;
                                logger.debug("Final authority string: {}", authorityString);
                                GrantedAuthority authority = new SimpleGrantedAuthority(authorityString);
                                List<GrantedAuthority> authorities = Collections.singletonList(authority);

                                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                        username, null, authorities);

                                authenticationToken
                                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                                logger.debug("User '{}' authenticated via JWT with role: {}", username,
                                        authorityString);
                            } else {
                                logger.warn(
                                        "JWT token validated but contained no username or role. Username: {}, Role: {}",
                                        username, role);
                            }
                        } else {
                            logger.warn("JWT token is invalid due to password change for user '{}'", username);
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write(
                                    "{\"error\": \"unauthorized\", \"message\": \"Password changed. Token expired.\"}");
                            return;
                        }
                    } else {
                        logger.warn("User '{}' not found in database", username);
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("{\"error\": \"unauthorized\", \"message\": \"User not found\"}");
                        return;
                    }

                } else {
                    logger.warn("Invalid JWT token for request to: {}", request.getRequestURI());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(
                            "\"{\\\"error\\\": \\\"unauthorized\\\", \\\"message\\\": \\\"Token is invalid or expired\\\"}\"");
                    return;
                }
            } else {
                logger.debug("No JWT token found in request to: {}", request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                response.getWriter().write("{\"error\": \"unauthorized\", \"message\": \"No token provided\"}");
                return;
            }

        } catch (Exception e) {
            logger.error("Could not set user authentication from JWT: {}", e.getMessage(), e);
            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            response.getWriter().write("{\"error\": \"unauthorized\", \"message\": \"Authentication failed\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}