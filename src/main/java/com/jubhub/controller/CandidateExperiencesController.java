package com.jubhub.controller;

import com.jubhub.common.ApiResponse;
import com.jubhub.model.dto.request.CandidateExperiencesRequest;
import com.jubhub.model.dto.response.CandidateExperiencesResponse;
import com.jubhub.security.CustomUserDetails;
import com.jubhub.service.interfaces.CandidateExperiencesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PastOrPresent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Experiences", description = "Quản lý kinh nhiệm của ứng viên")
@RestController
@RequestMapping("/api/candidates/experiences")
@RequiredArgsConstructor
public class CandidateExperiencesController {
    private final CandidateExperiencesService candidateExperiencesService;

    // Thêm kinh nhiệm mới
    @Operation(summary = "Thêm mới kinh nhiệm", description = "Tạo một mục kinh nhiệm mới cho ứng viên đăng nhập")
    @PostMapping
    public ResponseEntity<ApiResponse<CandidateExperiencesResponse>> createExperiences(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CandidateExperiencesRequest request) {
        CandidateExperiencesResponse response = candidateExperiencesService.createExperiences(userDetails.getUser(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thêm mới kinh nhiệm thành công", response));
    }

    // Lấy danh sách kinh nhiệm
    @Operation(summary = "Lấy danh sách kinh nhiệm", description = "Lấy tất cả kinh nhiệm của ứng viên đăng nhập")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CandidateExperiencesResponse>>> getExperiences(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CandidateExperiencesResponse> response = candidateExperiencesService.getCandidateExperiences(userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách kinh nhiệm thành công", response));
    }

    // Lấy chi tiết kinh nhiệm
    @Operation(summary = "Lấy chi tiết kinh nhiệm", description = "Lấy chi tiết kinh nhiệm của ứng viên đăng nhập")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CandidateExperiencesResponse>> getExperiencesById(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        CandidateExperiencesResponse response = candidateExperiencesService.getCandidateExperienceById(userDetails.getUser(), id);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết kinh nhiệm thành công", response));
    }

    // Cập nhật kinh nhiệm
    @Operation(summary = "Cập nhật kinh nhiệm", description = "Cập nhật kinh nhiệm của ứng viên đăng nhập")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CandidateExperiencesResponse>> updateExperiences(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CandidateExperiencesRequest request) {
        CandidateExperiencesResponse response = candidateExperiencesService.updateCandidateExperience(userDetails.getUser(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật kinh nhiệm thành công", response));
    }

    // Xóa kinh nhiệm
    @Operation(summary = "Cập nhật kinh nhiệm", description = "Cập nhật kinh nhiệm của ứng viên đăng nhập")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExperiences(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        candidateExperiencesService.deleteCandidateExperience(userDetails.getUser(), id);
        return ResponseEntity.ok(ApiResponse.success("Xóa kinh nhiệm thành công", null));
    }
}
