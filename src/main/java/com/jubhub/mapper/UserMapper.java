package com.jubhub.mapper;

import com.jubhub.model.dto.request.RegisterRequest;
import com.jubhub.model.dto.response.AuthResponse;
import com.jubhub.model.dto.response.UserResponse;
import com.jubhub.model.entity.Role;
import com.jubhub.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", expression = "java(mapRoles(user.getRoles()))")
    UserResponse toUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toUser(RegisterRequest request);

    default Set<String> mapRoles(Set<Role> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptySet();
        }
        return roles.stream()
                .filter(r -> r != null && r.getName() != null)
                .map(r -> r.getName().name())
                .collect(Collectors.toSet());
    }

    default AuthResponse toAuthResponse(String accessToken, String refreshToken, long expiresIn, UserResponse userResponse) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(userResponse)
                .build();
    }
}

