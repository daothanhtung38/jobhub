package com.jubhub.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillUpdateRequest {
    @NotBlank(message = "Tên kỹ năng không được để trống")
    @Size(max = 100, message = "Tên kỹ năng tối đa 100 ký tự")
    private String name;
    @Size(max = 100, message = "Slug tối đa 100 ký tự")
    private String slug;
    @Size(max = 100, message = "Danh mục tối đa 100 ký tự")
    private String category;
}
