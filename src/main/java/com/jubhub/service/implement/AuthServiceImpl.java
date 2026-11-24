package com.jubhub.service.implement;

import com.jubhub.exception.DuplicateResourceException;
import com.jubhub.mapper.UserMapper;
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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Collections;
import java.util.HexFormat;
import java.util.HashSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

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

        User user = userMapper.toUser(registerRequest);
        user.setEmail(email);
        user.setPasswordHash(encodedPassword);
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);
        user.setRoles(new HashSet<>(Collections.singletonList(role)));

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

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new IllegalArgumentException("Tài khoản của bạn đã bị tạm khóa (SUSPENDED)");
        }
        if (user.getStatus() == UserStatus.DELETED) {
            throw new IllegalArgumentException("Tài khoản của bạn đã bị xóa (DELETED)");
        }
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new IllegalArgumentException("Tài khoản chưa được kích hoạt (INACTIVE)");
        }

        // Cập nhật last_login_at
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        return createAuthResponse(user);
    }

    // REFRESH TOKEN
    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String tokenHash = hashToken(request.getRefreshToken());

        RefreshToken oldToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new DuplicateResourceException(
                        "Refresh token không hợp lệ", HttpStatus.UNAUTHORIZED
                ));

        if (oldToken.getRevokedAt() != null) {
            throw new DuplicateResourceException("Refresh token đã bị thu hồi", HttpStatus.UNAUTHORIZED);
        }

        if (oldToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(oldToken);
            throw new DuplicateResourceException("Refresh token đã hết hạn", HttpStatus.UNAUTHORIZED);
        }

        User user = oldToken.getUser();

        // Thu hồi token cũ (Token Rotation)
        oldToken.setRevokedAt(Instant.now());
        refreshTokenRepository.save(oldToken);

        return createAuthResponse(user);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        String tokenHash = hashToken(refreshToken);
        RefreshToken token = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new DuplicateResourceException(
                        "Không tìm thấy refresh token", HttpStatus.NOT_FOUND));

        token.setRevokedAt(Instant.now());
        refreshTokenRepository.save(token);
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

        // Lưu refresh token (dạng hash) vào bảng refresh_tokens
        saveRefreshToken(user, refreshToken);

        UserResponse userResponse = userMapper.toUserResponse(user);

        return userMapper.toAuthResponse(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpiration() / 1000,
                userResponse
        );
    }

    private void saveRefreshToken(User user, String rawToken) {
        String tokenHash = hashToken(rawToken);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .expiresAt(Instant.now().plusMillis(jwtService.getRefreshTokenExpiration()))
                .revokedAt(null)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
    }

    private String hashToken(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token không được để trống");
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Thuật toán SHA-256 không khả dụng trên hệ thống", e);
        }
    }
}
