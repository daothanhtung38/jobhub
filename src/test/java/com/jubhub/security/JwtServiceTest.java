package com.jubhub.security;

import com.jubhub.model.entity.Role;
import com.jubhub.model.entity.User;
import com.jubhub.model.enums.RoleType;
import com.jubhub.model.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    // Base64 encoded 256-bit secret key for testing
    private final String testSecret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long accessTokenExpiration = 900000;
    private final long refreshTokenExpiration = 604800000;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(testSecret, accessTokenExpiration, refreshTokenExpiration);
    }

    @Test
    void testGenerateAndValidateAccessToken() {
        Role candidateRole = Role.builder()
                .id(UUID.randomUUID())
                .name(RoleType.ROLE_CANDIDATE)
                .build();

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .email("test@jobhub.vn")
                .fullName("Nguyen Van A")
                .status(UserStatus.ACTIVE)
                .roles(new HashSet<>(Collections.singletonList(candidateRole)))
                .build();

        String token = jwtService.generateAccessToken(user);
        assertNotNull(token);

        String username = jwtService.extractUsername(token);
        assertEquals("test@jobhub.vn", username);

        UUID extractedUserId = jwtService.extractUserId(token);
        assertEquals(userId, extractedUserId);

        List<String> roles = jwtService.extractRoles(token);
        assertNotNull(roles);
        assertTrue(roles.contains("ROLE_CANDIDATE"));

        assertTrue(jwtService.isTokenValid(token, "test@jobhub.vn"));
        assertFalse(jwtService.isTokenValid(token, "other@jobhub.vn"));
    }

    @Test
    void testGenerateRefreshToken() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .email("refresh@jobhub.vn")
                .fullName("Le Van B")
                .status(UserStatus.ACTIVE)
                .build();

        String refreshToken = jwtService.generateRefreshToken(user);
        assertNotNull(refreshToken);

        String username = jwtService.extractUsername(refreshToken);
        assertEquals("refresh@jobhub.vn", username);

        UUID extractedUserId = jwtService.extractUserId(refreshToken);
        assertEquals(userId, extractedUserId);

        assertTrue(jwtService.isTokenValid(refreshToken, "refresh@jobhub.vn"));
    }
}

