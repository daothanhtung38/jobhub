package com.jubhub.model.entity;

import com.jubhub.model.enums.EmploymentStatus;
import com.jubhub.model.enums.Gender;
import com.jubhub.model.enums.ProfileVisibility;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "candidate_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "headline", length = 255)
    private String headline;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 20)
    private Gender gender;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "province_code", length = 20)
    private String provinceCode;

    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;

    @Builder.Default
    @Column(name = "years_of_experience", precision = 4, scale = 1)
    private BigDecimal yearsOfExperience = BigDecimal.ZERO;

    @Column(name = "expected_salary_min", precision = 15, scale = 2)
    private BigDecimal expectedSalaryMin;

    @Column(name = "expected_salary_max", precision = 15, scale = 2)
    private BigDecimal expectedSalaryMax;

    @Column(name = "preferred_job_title", length = 255)
    private String preferredJobTitle;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "employment_status", nullable = false, length = 30)
    private EmploymentStatus employmentStatus = EmploymentStatus.OPEN_TO_WORK;

    @Builder.Default
    @Column(name = "is_searchable", nullable = false)
    private Boolean isSearchable = false;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "profile_visibility", nullable = false, length = 20)
    private ProfileVisibility profileVisibility = ProfileVisibility.PRIVATE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CandidateEducation> educations = new HashSet<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CandidateExperience> experiences = new HashSet<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CandidateProject> projects = new HashSet<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CandidateCertification> certifications = new HashSet<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CandidateSkill> skills = new HashSet<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CandidateHiddenCompany> hiddenCompanies = new HashSet<>();

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<CandidateCv> cvs = new HashSet<>();
}

