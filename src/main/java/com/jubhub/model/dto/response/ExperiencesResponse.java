package com.jubhub.model.dto.response;

import com.jubhub.model.enums.EmploymentType;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperiencesResponse {
    private UUID id;

    private String companyName;

    private String jobTitle;

    private EmploymentType employmentType;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean isCurrent;

    private String description;

    private Instant createdAt;

    private Instant updatedAt;
}
