package com.project.user.config;

import com.project.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OncePerRequestFilter jwtAuthenticationFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                    @NonNull HttpServletRequest request,
                    @NonNull HttpServletResponse response,
                    @NonNull FilterChain filterChain
            ) throws ServletException, IOException {
                final String authHeader = request.getHeader("Authorization");
                final String gatewayUserEmail = request.getHeader("X-User-Email");
                final String gatewayUserRoles = request.getHeader("X-User-Roles");

                if (gatewayUserEmail != null && !gatewayUserEmail.isBlank()) {
                    // Gateway has already authenticated the request and passed identity headers
                    String role = (gatewayUserRoles != null && !gatewayUserRoles.isBlank()) ? gatewayUserRoles : "ROLE_CUSTOMER";
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            gatewayUserEmail,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority(role))
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    // Direct service call with JWT
                    final String jwt = authHeader.substring(7);
                    try {
                        final String userEmail = jwtService.extractUsername(jwt);
                        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                            userRepository.findByEmail(userEmail).ifPresent(user -> {
                                if (jwtService.isTokenValid(jwt, user.getEmail())) {
                                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                            user.getEmail(),
                                            null,
                                            Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
                                    );
                                    SecurityContextHolder.getContext().setAuthentication(authToken);
                                }
                            });
                        }
                    } catch (Exception e) {
                        // Invalid token, continue filter chain unauthenticated
                    }
                }
                filterChain.doFilter(request, response);
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)) // For H2 console
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/actuator/**",
                                "/h2-console/**",
                                "/api/v1/users/**" // Internal feign calls or protected by method security
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
