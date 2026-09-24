package com.workmate.workmate_ai.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        System.out.println("======================================");
        System.out.println("JWT FILTER REQUEST: " + requestUri);

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            System.out.println("JWT TOKEN: NOT PRESENT");

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7).trim();

        System.out.println("JWT TOKEN: PRESENT");

        if (token.isEmpty()) {

            System.out.println("JWT TOKEN: EMPTY");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (!jwtService.isTokenValid(token)) {

            System.out.println("JWT TOKEN: INVALID");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        System.out.println("JWT TOKEN: VALID");

        String email = jwtService.extractEmail(token);

        System.out.println("JWT EMAIL: " + email);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        null,
                        Collections.emptyList()
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        System.out.println("SECURITY CONTEXT: AUTHENTICATED");

        filterChain.doFilter(request, response);

        System.out.println("JWT FILTER RESPONSE STATUS: " + response.getStatus());
        System.out.println("======================================");
    }
}