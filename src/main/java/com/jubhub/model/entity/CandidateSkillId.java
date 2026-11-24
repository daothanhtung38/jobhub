package com.jubhub.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class CandidateSkillId implements Serializable {

    @Column(name = "candidate_id")
    private UUID candidateId;

    @Column(name = "skill_id")
    private UUID skillId;
}

