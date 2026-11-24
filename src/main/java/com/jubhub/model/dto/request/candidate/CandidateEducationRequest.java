package com.jubhub.model.dto.request.candidate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Yêu cầu thêm mới hoặc cập nhật học vấn")
public class CandidateEducationRequest {

    @NotBlank(message = "Tên trường không được để trống")
    @Size(max = 255, message = "Tên trường tối đa 255 ký tự")
    @Schema(description = "Tên trường học / đại học", example = "Đại học Bách Khoa Hà Nội")
    private String schoolName;

    @Size(max = 100, message = "Bằng cấp tối đa 100 ký tự")
    @Schema(description = "Bằng cấp / Trình độ", example = "Cử nhân")
    private String degree;

    @Size(max = 150, message = "Chuyên ngành tối đa 150 ký tự")
    @Schema(description = "Chuyên ngành đào tạo", example = "Công nghệ Thông tin")
    private String major;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @Schema(description = "Ngày bắt đầu học", example = "2018-09-01")
    private LocalDate startDate;

    @Schema(description = "Ngày tốt nghiệp / kết thúc", example = "2022-06-30")
    private LocalDate endDate;

    @Schema(description = "Hiện tại vẫn đang học tại đây", example = "false")
    private Boolean isCurrent;

    @Schema(description = "Mô tả chi tiết / Thành tích", example = "GPA: 3.5/4.0. Đạt giải khuyến khích NCKH cấp trường.")
    private String description;
}