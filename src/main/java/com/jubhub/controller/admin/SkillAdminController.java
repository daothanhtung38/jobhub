package com.jubhub.controller.admin;

import com.jubhub.common.ApiResponse;
import com.jubhub.model.dto.request.SkillCreateRequest;
import com.jubhub.model.dto.request.SkillUpdateRequest;
import com.jubhub.model.dto.response.SkillResponse;
import com.jubhub.service.interfaces.admin.SkillAdminService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/skill")
@RequiredArgsConstructor
public class SkillAdminController {
    private final SkillAdminService skillService;

    @Operation(summary = "Thêm kĩ năng mới")
    @PostMapping
    public ResponseEntity<ApiResponse<SkillResponse>> createSkill(@Valid  SkillCreateRequest request) {
        SkillResponse response = skillService.createSkill(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Thêm mới kĩ năng thành công", response));
    }

    @Operation(summary = "lấy danh sách kĩ năng")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<SkillResponse>>> getAllSkills(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<SkillResponse> response = skillService.getAllSkills(page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách kĩ năng thành công", response));
    }

    @Operation(summary = "Lấy chi tiết kĩ năng")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillResponse>> getSkillById(@PathVariable("id") UUID id) {
        SkillResponse response = skillService.getSkillById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiêt kĩ năng thành công", response));
    }

    @Operation(summary = "Cập nhật kĩ năng")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillResponse>> updateSkill(@Valid @RequestBody SkillUpdateRequest request, @PathVariable UUID id) {
        SkillResponse response = skillService.updateSkill(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật kĩ năng thành công", response));
    }

    @Operation(summary = "Xóa kĩ năng")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(@PathVariable UUID id) {
        skillService.deleteSkill(id);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật kĩ năng thành công", null));
    }
}
