// filepath: api-gateway/src/main/java/com/example/apigateway/filter/AuthenticationFilter.java
package com.example.gateway.filter;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.example.gateway.util.JwtUtil;

import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
    private static final String BEARER_PREFIX = "Bearer ";

    // Define whitelist paths that don't need authentication
    private static final List<String> WHITELIST_PATHS = Arrays.asList(
            "/api/auth/login",
            // "/api/auth/register",
            "/api/auth/refresh-token",
            "/actuator/health",
            "/api/auth/reset-password",
            "/api/auth/forgot-password");

    private final JwtUtil jwtUtil;

    public AuthenticationFilter(JwtUtil jwtUtil) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            // Check if path is in whitelist
            String path = request.getPath().value();
            if (WHITELIST_PATHS.stream().anyMatch(path::contains)) {
                logger.debug("Path {} is whitelisted, allowing request", path);
                return chain.filter(exchange);
            }

            // Extract and validate token
            String token = extractToken(request);
            if (token == null) {
                logger.error("No authorization token found in request");
                return onError(exchange, "Authorization header is missing", HttpStatus.UNAUTHORIZED);
            }

            // Validate JWT token
            try {
                if (!jwtUtil.validateToken(token)) {
                    logger.error("Invalid JWT token");
                    return onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED);
                }

                logger.debug(token);
                System.out.println("thêm các thông tin cần thiết \n \n \n ");
                System.out.println("Token : " + token + "\n \n \n");

                // Add user info to headers
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .header("X-User-Username", jwtUtil.getUsernameFromToken(token))
                        .header("X-User-Role", jwtUtil.getRolesFromToken(token))
                        .header("X-Teacher-Code", jwtUtil.getTeacherCodeFromToken(token))
                        .build();

                System.out.println(modifiedRequest + "\n \n");

                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            } catch (Exception e) {
                logger.error("Error processing JWT token", e);
                return onError(exchange, "Error processing token", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            return authHeader.substring(BEARER_PREFIX.length());
        }

        if (request.getCookies().containsKey("access_token")) {
            return request.getCookies().getFirst("access_token").getValue();
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String errorJson = String.format("{\"error\": \"%s\"}", message);

        DataBuffer buffer = response.bufferFactory().wrap(errorJson.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    public static class Config {
        // Configuration properties if needed
    }
}