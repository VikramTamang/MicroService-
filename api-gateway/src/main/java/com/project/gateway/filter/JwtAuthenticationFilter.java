package com.project.gateway.filter;

import com.project.gateway.config.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private static final List<String> PUBLIC_PREFIXES = List.of(
            "/api/v1/auth/",
            "/actuator",
            "/v3/api-docs",
            "/swagger-ui"
    );

    private static final List<String> INTERNAL_ONLY_PREFIXES = List.of(
            "/api/v1/products/reserve-stock",
            "/api/v1/products/release-stock",
            "/api/v1/internal"
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

        // Block direct external access to internal inter-service endpoints
        if (isInternalOnlyEndpoint(path)) {
            log.warn("Direct external access blocked for internal inter-service endpoint: {}", path);
            return onError(exchange, HttpStatus.FORBIDDEN, "Forbidden: Direct access to internal inter-service endpoints is prohibited");
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
        Object sellerIdObj = claims.get("sellerId");
        String sellerId = sellerIdObj != null ? String.valueOf(sellerIdObj) : "";
        String role = (String) claims.get("role");

        // Role-based authorization for Admin endpoints
        if (isAdminOnlyEndpoint(path, method) && !"ROLE_ADMIN".equalsIgnoreCase(role)) {
            log.warn("Access denied for user {} (role={}) to admin endpoint: {} {}", userEmail, role, method, path);
            return onError(exchange, HttpStatus.FORBIDDEN, "Forbidden: Administrative privileges required");
        }

        // Role-based authorization for Seller endpoints
        if (isSellerOnlyEndpoint(path) && !"ROLE_SELLER".equalsIgnoreCase(role) && !"ROLE_ADMIN".equalsIgnoreCase(role)) {
            log.warn("Access denied for user {} (role={}) to seller endpoint: {} {}", userEmail, role, method, path);
            return onError(exchange, HttpStatus.FORBIDDEN, "Forbidden: Seller store privileges required");
        }

        // Role-based authorization for Image Uploads
        if (path.equals("/api/v1/products/upload-image") && !"ROLE_SELLER".equalsIgnoreCase(role) && !"ROLE_ADMIN".equalsIgnoreCase(role)) {
            log.warn("Access denied for user {} (role={}) to image upload: {} {}", userEmail, role, method, path);
            return onError(exchange, HttpStatus.FORBIDDEN, "Forbidden: Seller or Admin privileges required for file uploads");
        }

        // Mutate request to pass authenticated identity headers downstream
        var builder = request.mutate()
                .header("X-User-Id", userId)
                .header("X-User-Email", userEmail)
                .header("X-User-Roles", role != null ? role : "ROLE_CUSTOMER");

        if (!sellerId.isBlank()) {
            builder.header("X-Seller-Id", sellerId);
        }

        ServerHttpRequest mutatedRequest = builder.build();
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private boolean isInternalOnlyEndpoint(String path) {
        for (String prefix : INTERNAL_ONLY_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPublicEndpoint(String path, HttpMethod method) {
        for (String prefix : PUBLIC_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        // Public Catalog GET and HEAD operations (products, categories, images, public sellers)
        if ((method == HttpMethod.GET || method == HttpMethod.HEAD)) {
            if (path.startsWith("/api/v1/products") || path.startsWith("/api/v1/categories")) {
                return true;
            }
            if (path.matches("^/api/v1/sellers/\\d+$")) {
                return true;
            }
        }
        return false;
    }

    private boolean isAdminOnlyEndpoint(String path, HttpMethod method) {
        if (path.startsWith("/api/v1/admin/")) {
            return true;
        }
        if (path.equals("/api/v1/users") && method == HttpMethod.GET) {
            return true;
        }
        // Mutating category endpoints
        if (path.startsWith("/api/v1/categories") && (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.DELETE)) {
            return true;
        }
        // Mutating product endpoints (except seller endpoints starting with /api/v1/seller/ and upload-image)
        if (path.startsWith("/api/v1/products") && !path.startsWith("/api/v1/products/images/") && !path.equals("/api/v1/products/upload-image")) {
            if (method == HttpMethod.POST || method == HttpMethod.PUT || method == HttpMethod.PATCH || method == HttpMethod.DELETE) {
                return true;
            }
        }
        return false;
    }

    private boolean isSellerOnlyEndpoint(String path) {
        return path.startsWith("/api/v1/seller/");
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
