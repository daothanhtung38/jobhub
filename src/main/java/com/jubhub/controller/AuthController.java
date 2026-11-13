package com.jubhub.controller;

import com.jubhub.common.ApiResponse;
import com.jubhub.model.dto.request.LoginRequest;
import com.jubhub.model.dto.request.RefreshTokenRequest;
import com.jubhub.model.dto.request.RegisterRequest;
import com.jubhub.model.dto.response.AuthResponse;
import com.jubhub.service.interfaces.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Các API xác thực và phân quyền")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Đăng ký tài khoản mới", description = "Đăng ký tài khoản với vai trò ROLE_CANDIDATE (mặc định) hoặc ROLE_RECRUITER")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký tài khoản thành công", response));
    }

    @Operation(summary = "Đăng nhập", description = "Đăng nhập hệ thống bằng email và mật khẩu")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
    }

    @Operation(summary = "refresh token")
    @PostMapping("/refreshToken")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logout")
    @PostMapping("/Logout")
    public ResponseEntity<Void> logout(@Valid RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }
}
