package com.jubhub.model.dto.request;

import com.jubhub.model.enums.ProficiencyLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateSkillCreateRequest {
    @NotNull(message = "ID kỹ năng không được để trống")
    @Schema(description = "ID của kỹ năng lấy từ API tìm kiếm", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID skillId;
    @NotNull(message = "Trình độ kỹ năng không được để trống")
    @Schema(description = "Trình độ: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT", example = "ADVANCED")
    private ProficiencyLevel proficiencyLevel;
    @DecimalMin(value = "0.0", message = "Số năm kinh nghiệm không được âm")
    @Schema(description = "Số năm kinh nghiệm với kỹ năng này", example = "2.5")
    private BigDecimal yearsOfExperience;
}

