package com.jubhub.service.interfaces;

import com.jubhub.model.dto.request.LoginRequest;
import com.jubhub.model.dto.request.RefreshTokenRequest;
import com.jubhub.model.dto.request.RegisterRequest;
import com.jubhub.model.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
    void logout(String refreshToken);
}
