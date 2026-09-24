package com.workmate.workmate_ai.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @Test
    void generateToken_shouldCreateValidToken() {

        // Arrange
        String secret =
                "V29ya01hdGVBSV9zdXBlcl9zZWNyZXRfa2V5XzMyX2J5dGVzX2xvbmc=";

        JwtService jwtService =
                new JwtService(secret);

        String email = "test@example.com";

        // Act
        String token =
                jwtService.generateToken(email);

        // Assert
        assertNotNull(token);
        assertFalse(token.isBlank());

        assertTrue(
                jwtService.isTokenValid(token)
        );

        assertEquals(
                email,
                jwtService.extractEmail(token)
        );
    }

    @Test
    void isTokenValid_shouldReturnFalse_forInvalidToken() {

        // Arrange
        String secret =
                "V29ya01hdGVBSV9zdXBlcl9zZWNyZXRfa2V5XzMyX2J5dGVzX2xvbmc=";

        JwtService jwtService =
                new JwtService(secret);

        String invalidToken = "this.is.not.a.valid.jwt";

        // Act
        boolean result =
                jwtService.isTokenValid(invalidToken);

        // Assert
        assertFalse(result);
    }
}