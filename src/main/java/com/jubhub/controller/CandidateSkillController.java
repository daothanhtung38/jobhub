package com.jubhub.controller;

import com.jubhub.common.ApiResponse;
import com.jubhub.model.dto.request.CandidateSkillCreateRequest;
import com.jubhub.model.dto.request.CandidateSkillUpdateRequest;
import com.jubhub.model.dto.response.CandidateSkillResponse;
import com.jubhub.security.CustomUserDetails;
import com.jubhub.service.interfaces.CandidateSkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Candidate Skill", description = "Quản lý kĩ năng ứng viên")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/candidates/skills")
public class CandidateSkillController {
    private final CandidateSkillService candidateSkillService;

    @Operation(summary = "Xem danh sách skill", description = "Lấy toàn bộ thông tin kĩ năng của ứng viên đang đăng nhập")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CandidateSkillResponse>>> getCandidateSkill(
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        List<CandidateSkillResponse> response = candidateSkillService.getSkill(userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách kĩ năng thành công", response));
    }

    @Operation(summary = "Thêm mới kĩ năng", description = "Thêm mới kĩ năng của ứng viên đang đăng nhập")
    @PostMapping
    public ResponseEntity<ApiResponse<CandidateSkillResponse>> createCandidateSkill(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CandidateSkillCreateRequest request
            ) {
        CandidateSkillResponse response = candidateSkillService.createCandidateSkill(userDetails.getUser(), request);
        return ResponseEntity.ok(ApiResponse.success("Thêm mới kĩ năng thành công", response));
    }

    @Operation(summary = "Cập nhật trình độ kỹ năng", description = "Cập nhật trình độ (proficiency) và số năm kinh nghiệm")
    @PutMapping("/{skillId}")
    public ResponseEntity<ApiResponse<CandidateSkillResponse>> updateSkill(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("skillId") UUID skillId,
            @Valid @RequestBody CandidateSkillUpdateRequest request) {
        CandidateSkillResponse response = candidateSkillService.updateCandidateSkill(userDetails.getUser(), skillId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật kỹ năng thành công", response));
    }
    @Operation(summary = "Xóa kỹ năng khỏi hồ sơ", description = "Xóa một kỹ năng đã thêm")
    @DeleteMapping("/{skillId}")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("skillId") UUID skillId) {
        candidateSkillService.deleteCandidateSkill(userDetails.getUser(), skillId);
        return ResponseEntity.ok(ApiResponse.success("Xóa kỹ năng khỏi hồ sơ thành công", null));
    }
}
