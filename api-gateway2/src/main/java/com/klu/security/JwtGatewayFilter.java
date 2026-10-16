package com.klu.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtGatewayFilter
        implements org.springframework.cloud.gateway.filter.GlobalFilter {

    private final SecretKey key;

    public JwtGatewayFilter(
            @Value("${jwt.secret}") String secret) {

        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        String path = exchange
                .getRequest()
                .getURI()
                .getPath();

        // Public authentication endpoints
        // OPTIONS is allowed for CORS preflight requests
        if (exchange.getRequest()
                .getMethod()
                .name()
                .equals("OPTIONS")

                || path.startsWith("/api/auth/register")

                || path.equals("/api/auth/login")

                || path.startsWith("/api/auth/forgot-password")

                || path.equals("/api/auth/reset-password")) {

            return chain.filter(exchange);
        }

        // Get Authorization header
        String authHeader = exchange
                .getRequest()
                .getHeaders()
                .getFirst("Authorization");

        if (authHeader == null
                || !authHeader.startsWith("Bearer ")) {

            return unauthorized(exchange);
        }

        String token = authHeader.substring(7);

        try {

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Token is valid
            String phoneNumber = claims.getSubject();

            ServerHttpRequest request = exchange
                    .getRequest()
                    .mutate()
                    .header("X-User-Phone", phoneNumber)
                    .build();

            return chain.filter(
                    exchange.mutate()
                            .request(request)
                            .build()
            );

        } catch (Exception e) {

            return unauthorized(exchange);
        }
    }

    private Mono<Void> unauthorized(
            ServerWebExchange exchange) {

        exchange.getResponse()
                .setStatusCode(
                        HttpStatus.UNAUTHORIZED
                );

        return exchange.getResponse()
                .setComplete();
    }
}