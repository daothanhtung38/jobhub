package com.jubhub.model.dto.request;

import com.jubhub.model.enums.ProficiencyLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CandidateSkillUpdateRequest {
    @NotNull(message = "Trình độ kỹ năng không được để trống")
    @Schema(description = "Trình độ: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT", example = "EXPERT")
    private ProficiencyLevel proficiencyLevel;
    @DecimalMin(value = "0.0", message = "Số năm kinh nghiệm không được âm")
    @Schema(description = "Số năm kinh nghiệm với kỹ năng này", example = "3.0")
    private BigDecimal yearsOfExperience;
}
