package com.jubhub.model.dto.response;

import com.jubhub.model.enums.EmploymentStatus;
import com.jubhub.model.enums.Gender;
import com.jubhub.model.enums.ProfileVisibility;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProfileResponse {
    // Thông tin từ User
    private UUID userId;
    private String email;
    private String fullName;
    private String phone;
    private String avatarUrl;

    // Thông tin từ CandidateProfile
    private UUID profileId;
    private String headline;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String provinceCode;
    private String summary;
    private BigDecimal yearsOfExperience;
    private BigDecimal expectedSalaryMin;
    private BigDecimal expectedSalaryMax;
    private String preferredJobTitle;
    private EmploymentStatus employmentStatus;
    private Boolean isSearchable;
    private ProfileVisibility profileVisibility;
    private Instant createdAt;
    private Instant updatedAt;
}
