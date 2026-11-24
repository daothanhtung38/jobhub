package com.jubhub.model.entity;

import com.jubhub.model.enums.ProficiencyLevel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "candidate_skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateSkill {

    @EmbeddedId
    private CandidateSkillId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("candidateId")
    @JoinColumn(name = "candidate_id", nullable = false)
    private CandidateProfile candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillId")
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Enumerated(EnumType.STRING)
    @Column(name = "proficiency_level", length = 30)
    private ProficiencyLevel proficiencyLevel;

    @Builder.Default
    @Column(name = "years_of_experience", precision = 4, scale = 1)
    private BigDecimal yearsOfExperience = BigDecimal.ZERO;
}

