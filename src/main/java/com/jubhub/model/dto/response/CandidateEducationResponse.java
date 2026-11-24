package com.jubhub.model.dto.response;

import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateEducationResponse {
    private UUID id;
    private String schoolName;
    private String degree;
    private String major;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}