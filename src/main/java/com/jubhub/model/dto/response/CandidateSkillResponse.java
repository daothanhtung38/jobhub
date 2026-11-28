package com.jubhub.model.dto.response;

import com.jubhub.model.enums.ProficiencyLevel;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateSkillResponse {
    private UUID skillId;
    private String name;
    private String slug;
    private String category;
    private ProficiencyLevel proficiencyLevel;
    private BigDecimal yearsOfExperience;
}
