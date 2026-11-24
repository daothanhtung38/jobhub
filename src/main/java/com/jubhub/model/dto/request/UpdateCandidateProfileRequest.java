package com.jubhub.model.dto.request;

import com.jubhub.model.enums.EmploymentStatus;
import com.jubhub.model.enums.Gender;
import com.jubhub.model.enums.ProfileVisibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Yêu cầu cập nhật hồ sơ ứng viên")
public class UpdateCandidateProfileRequest {
    // Cập nhật bảng User
    @Size(max = 150, message = "Họ tên tối đa 150 ký tự")
    @Schema(description = "Họ và tên đầy đủ")
    private String fullName;

    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại không hợp lệ")
    @Schema(description = "Số điện thoại liên hệ")
    private String phone;

    @Schema(description = "Đường dẫn ảnh đại diện")
    private String avatarUrl;

    // Cập nhật bảng CandidateProfile
    @Size(max = 255, message = "Tiêu đề hồ sơ tối đa 255 ký tự")
    @Schema(description = "Tiêu đề hồ sơ / chức danh hiện tại")
    private String headline;

    @Past(message = "Ngày sinh phải là một ngày trong quá khứ")
    @Schema(description = "Ngày sinh", example = "1998-05-20")
    private LocalDate dateOfBirth;

    @Schema(description = "Giới tính: MALE, FEMALE, OTHER")
    private Gender gender;

    @Schema(description = "Địa chỉ cụ thể")
    private String address;

    @Size(max = 20, message = "Mã tỉnh/thành tối đa 20 ký tự")
    @Schema(description = "Mã tỉnh / thành phố")
    private String provinceCode;

    @Schema(description = "Tóm tắt bản thân / mục tiêu nghề nghiệp", example = "Lập trình viên hơn 2 năm kinh nghiệm với Spring Boot và PostgreSQL...")
    private String summary;

    @DecimalMin(value = "0.0", message = "Số năm kinh nghiệm không được âm")
    @Digits(integer = 3, fraction = 1, message = "Số năm kinh nghiệm không hợp lệ (ví dụ: 2.5)")
    @Schema(description = "Số năm kinh nghiệm")
    private BigDecimal yearsOfExperience;

    @DecimalMin(value = "0.0", message = "Mức lương tối thiểu không được âm")
    @Schema(description = "Mức lương mong muốn tối thiểu (VNĐ)")
    private BigDecimal expectedSalaryMin;

    @DecimalMin(value = "0.0", message = "Mức lương tối đa không được âm")
    @Schema(description = "Mức lương mong muốn tối đa (VNĐ)", example = "25000000")
    private BigDecimal expectedSalaryMax;

    @Size(max = 255, message = "Vị trí mong muốn tối đa 255 ký tự")
    @Schema(description = "Vị trí công việc mong muốn", example = "Java Developer")
    private String preferredJobTitle;

    @Schema(description = "Trạng thái tìm việc: OPEN_TO_WORK, NOT_LOOKING, LOOKING_ACTIVELY")
    private EmploymentStatus employmentStatus;

    @Schema(description = "Cho phép nhà tuyển dụng tìm thấy hồ sơ hay không")
    private Boolean isSearchable;

    @Schema(description = "Chế độ hiển thị hồ sơ: PUBLIC, PRIVATE, RECRUITER_ONLY")
    private ProfileVisibility profileVisibility;
}
