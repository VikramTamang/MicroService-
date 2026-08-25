package com.project.gateway.filter;

import com.project.gateway.config.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/api/v1/auth/",
            "/actuator",
            "/v3/api-docs",
            "/swagger-ui"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        HttpMethod method = request.getMethod();

        log.debug("Gateway routing request: {} {}", method, path);

        // Allow CORS pre-flight requests
        if (method == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        boolean isPublic = isPublicEndpoint(path, method);
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            if (isPublic) {
                return chain.filter(exchange);
            }
            log.warn("Missing or invalid Authorization header for protected path: {}", path);
            return onError(exchange, HttpStatus.UNAUTHORIZED, "Unauthorized: Authentication token is missing or malformed");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            if (isPublic) {
                return chain.filter(exchange);
            }
            log.warn("Invalid or expired JWT token for path: {}", path);
            return onError(exchange, HttpStatus.UNAUTHORIZED, "Unauthorized: Token is invalid or expired");
        }

        Claims claims = jwtUtil.extractAllClaims(token);
        String userEmail = claims.getSubject();
        Object userIdObj = claims.get("userId");
        String userId = userIdObj != null ? String.valueOf(userIdObj) : "";
        String role = (String) claims.get("role");

        // Role-based authorization for Admin endpoints
        if (isAdminOnlyEndpoint(path, method) && !"ROLE_ADMIN".equalsIgnoreCase(role)) {
            log.warn("Access denied for user {} (role={}) to admin endpoint: {} {}", userEmail, role, method, path);
            return onError(exchange, HttpStatus.FORBIDDEN, "Forbidden: Administrative privileges required");
        }

        // Mutate request to pass authenticated identity headers downstream
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id", userId)
                .header("X-User-Email", userEmail)
                .header("X-User-Roles", role != null ? role : "ROLE_CUSTOMER")
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private boolean isPublicEndpoint(String path, HttpMethod method) {
        for (String prefix : PUBLIC_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        // Public Catalog GET operations
        if (method == HttpMethod.GET && (path.startsWith("/api/v1/products") || path.startsWith("/api/v1/categories"))) {
            return true;
        }
        return false;
    }

    private boolean isAdminOnlyEndpoint(String path, HttpMethod method) {
        if ((path.startsWith("/api/v1/products") || path.startsWith("/api/v1/categories"))
                && (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.DELETE || method == HttpMethod.PATCH)) {
            // Reserve-stock is internal inter-service call, permit
            if (path.contains("/reserve-stock")) {
                return false;
            }
            return true;
        }
        if (path.equals("/api/v1/users") && method == HttpMethod.GET) {
            return true;
        }
        return false;
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format(
                "{\"success\":false,\"message\":\"%s\",\"timestamp\":\"%s\"}",
                message,
                java.time.LocalDateTime.now()
        );

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100; // Run early in filter chain
    }
}
