package com.jubhub.service;

import com.jubhub.exception.DuplicateResourceException;
import com.jubhub.mapper.UserMapper;
import com.jubhub.mapper.UserMapperImpl;
import com.jubhub.model.dto.request.RegisterRequest;
import com.jubhub.model.dto.response.AuthResponse;
import com.jubhub.model.entity.Role;
import com.jubhub.model.entity.User;
import com.jubhub.model.enums.RoleType;
import com.jubhub.model.enums.UserStatus;
import com.jubhub.respository.RefreshTokenRepository;
import com.jubhub.respository.RoleRepository;
import com.jubhub.respository.UserRepository;
import com.jubhub.security.JwtService;
import com.jubhub.service.implement.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @InjectMocks
    private AuthServiceImpl authService;

    private Role candidateRole;

    @BeforeEach
    void setUp() {
        candidateRole = Role.builder()
                .id(UUID.randomUUID())
                .name(RoleType.ROLE_CANDIDATE)
                .description("Vai trò ứng viên tìm việc")
                .build();
    }

    @Test
    void testRegisterCandidateSuccess() {
        RegisterRequest request = RegisterRequest.builder()
                .email("candidate@jobhub.vn")
                .password("Password@123")
                .fullName("Nguyen Van Ung Vien")
                .phone("0912345678")
                .role("ROLE_CANDIDATE")
                .build();

        when(userRepository.existsByEmail("candidate@jobhub.vn")).thenReturn(false);
        when(roleRepository.findByName(RoleType.ROLE_CANDIDATE)).thenReturn(Optional.of(candidateRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        User savedUser = User.builder()
                .id(UUID.randomUUID())
                .email("candidate@jobhub.vn")
                .passwordHash("encodedPassword")
                .fullName("Nguyen Van Ung Vien")
                .phone("0912345678")
                .status(UserStatus.ACTIVE)
                .emailVerified(false)
                .createdAt(Instant.now())
                .roles(java.util.Collections.singleton(candidateRole))
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateAccessToken(savedUser)).thenReturn("dummy-access-token");
        when(jwtService.generateRefreshToken(savedUser)).thenReturn("dummy-refresh-token");
        when(jwtService.getAccessTokenExpiration()).thenReturn(900000L);
        when(jwtService.getRefreshTokenExpiration()).thenReturn(604800000L);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("dummy-access-token", response.getAccessToken());
        assertEquals("dummy-refresh-token", response.getRefreshToken());
        assertEquals("candidate@jobhub.vn", response.getUser().getEmail());
        assertTrue(response.getUser().getRoles().contains("ROLE_CANDIDATE"));
        assertFalse(response.getUser().getEmailVerified());

        verify(refreshTokenRepository, times(1)).save(any());
    }

    @Test
    void testRegisterDuplicateEmailThrowsException() {
        RegisterRequest request = RegisterRequest.builder()
                .email("existing@jobhub.vn")
                .password("Password@123")
                .fullName("Duplicate User")
                .build();

        when(userRepository.existsByEmail("existing@jobhub.vn")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testRegisterAdminRoleRejected() {
        RegisterRequest request = RegisterRequest.builder()
                .email("hacker@jobhub.vn")
                .password("Password@123")
                .fullName("Hacker")
                .role("ROLE_ADMIN")
                .build();

        when(userRepository.existsByEmail("hacker@jobhub.vn")).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        assertTrue(exception.getMessage().contains("ROLE_ADMIN"));
        verify(userRepository, never()).save(any());
    }
}

