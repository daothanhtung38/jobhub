package com.jubhub.model.dto.request;

import com.jubhub.model.enums.EmploymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExperiencesRequest {
    @NotBlank(message = "Tên công ty không được để trống")
    @Size(max = 255, message = "Tên công ty không được vượt quá 255 ký tự")
    private String companyName;

    @NotBlank(message = "Vị trí công việc không được để trống")
    @Size(max = 150, message = "Vị trí công việc không được vượt quá 150 ký tự")
    private String jobTitle;

    private EmploymentType employmentType;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @PastOrPresent(message = "Ngày bắt đầu không được lớn hơn ngày hiện tại")
    private LocalDate startDate;

    @PastOrPresent(message = "Ngày kết thúc không được lớn hơn ngày hiện tại")
    private LocalDate endDate;

    @Builder.Default
    private Boolean isCurrent = false;

    private String description;
}
