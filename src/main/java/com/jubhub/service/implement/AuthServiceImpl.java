package com.jubhub.service.implement;

import com.jubhub.exception.DuplicateResourceException;
import com.jubhub.model.dto.request.LoginRequest;
import com.jubhub.model.dto.request.RefreshTokenRequest;
import com.jubhub.model.dto.request.RegisterRequest;
import com.jubhub.model.dto.response.AuthResponse;
import com.jubhub.model.dto.response.UserResponse;
import com.jubhub.model.entity.RefreshToken;
import com.jubhub.model.entity.Role;
import com.jubhub.model.entity.User;
import com.jubhub.model.enums.RoleType;
import com.jubhub.model.enums.UserStatus;
import com.jubhub.respository.RefreshTokenRepository;
import com.jubhub.respository.RoleRepository;
import com.jubhub.respository.UserRepository;
import com.jubhub.security.JwtService;
import com.jubhub.service.interfaces.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        String email = registerRequest.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email này đã được sử dụng", HttpStatus.CONFLICT);
        }

        RoleType roleType = resolveRole(registerRequest.getRole());
        Role role = roleRepository.findByName(roleType)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(roleType)
                                .description(getDefaultRoleDescription(roleType))
                                .isSystem(true)
                                .build()
                ));

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());

        User user = User.builder()
                .email(email)
                .passwordHash(encodedPassword)
                .fullName(registerRequest.getFullName().trim())
                .phone(registerRequest.getPhone() != null ? registerRequest.getPhone().trim() : null)
                .status(UserStatus.ACTIVE)
                .emailVerified(false)
                .roles(new HashSet<>(Collections.singletonList(role)))
                .build();

        User savedUser = userRepository.save(user);

        return createAuthResponse(savedUser);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Email hoặc mật khẩu không chính xác"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Email hoặc mật khẩu không chính xác");
        }

        if (user.getStatus() == UserStatus.LOCKED) {
            throw new IllegalArgumentException("Tài khoản của bạn đã bị khóa");
        }

        return createAuthResponse(user);
    }

    // REFRESH TOKEN
    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken oldToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new DuplicateResourceException(
                        "Refresh token không hợp lệ", HttpStatus.UNAUTHORIZED
                ));
        if (oldToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(oldToken);
            throw new DuplicateResourceException("RefreshToken hết hạn", HttpStatus.UNAUTHORIZED);
        }

        User user = oldToken.getUser();

        refreshTokenRepository.deleteByToken(oldToken.getToken());

        return createAuthResponse(user);
    }

    @Override
    public void logout(String refreshToken) {
        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new DuplicateResourceException(
                        "Không tìm thấy refresh token ", HttpStatus.NOT_FOUND));

        refreshTokenRepository.delete(token);
    }

    private RoleType resolveRole(String roleInput) {
        if (roleInput == null || roleInput.trim().isEmpty()) {
            return RoleType.ROLE_CANDIDATE; // Mặc định là ứng viên
        }

        String normalized = roleInput.trim().toUpperCase();

        if (normalized.equals("ADMIN") || normalized.equals("ROLE_ADMIN")) {
            throw new IllegalArgumentException("Không thể tự đăng ký tài khoản với vai trò Quản trị viên (ROLE_ADMIN)");
        }

        if (normalized.equals("CANDIDATE") || normalized.equals("ROLE_CANDIDATE")) {
            return RoleType.ROLE_CANDIDATE;
        }

        if (normalized.equals("RECRUITER") || normalized.equals("ROLE_RECRUITER")) {
            return RoleType.ROLE_RECRUITER;
        }

        throw new IllegalArgumentException("Vai trò không hợp lệ: '" + roleInput + "'. Chỉ chấp nhận ROLE_CANDIDATE hoặc ROLE_RECRUITER");
    }

    private String getDefaultRoleDescription(RoleType roleType) {
        return switch (roleType) {
            case ROLE_CANDIDATE -> "Vai trò ứng viên tìm việc";
            case ROLE_RECRUITER -> "Vai trò nhà tuyển dụng";
            case ROLE_ADMIN -> "Vai trò quản trị viên hệ thống";
        };
    }

    private AuthResponse createAuthResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Lưu refresh token vào bảng refresh_tokens
        saveRefreshToken(user, refreshToken);

        Set<String> roleNames = user.getRoles() != null
                ? user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet())
                : Collections.emptySet();

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .emailVerified(user.getEmailVerified())
                .roles(roleNames)
                .createdAt(user.getCreatedAt())
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration() / 1000)
                .user(userResponse)
                .build();
    }

    private void saveRefreshToken(User user, String token) {
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .token(token)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtService.getRefreshTokenExpiration() / 1000))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
    }
}
