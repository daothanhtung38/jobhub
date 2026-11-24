package com.jubhub.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jubhub.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String phone;
    private String avatarUrl;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    private UserStatus status;
    private Set<String> roles;

    @JsonProperty("last_login_at")
    private Instant lastLoginAt;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("updated_at")
    private Instant updatedAt;

    // Getter tương thích ngược cho isEmailVerified
    @JsonProperty("is_email_verified")
    public Boolean getIsEmailVerified() {
        return emailVerified;
    }
}
