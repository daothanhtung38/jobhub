package com.jubhub.controller;

import com.jubhub.common.ApiResponse;
import com.jubhub.model.dto.request.candidate.CandidateEducationRequest;
import com.jubhub.model.dto.response.CandidateEducationResponse;
import com.jubhub.model.entity.CandidateEducation;
import com.jubhub.security.CustomUserDetails;
import com.jubhub.service.interfaces.CandidateEducationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Education", description = "Quản lý học vấn ứng viên")
@RestController
@RequestMapping("/api/candidates/education")
@RequiredArgsConstructor
public class CandidateEducationController {
    private final CandidateEducationService candidateEducationService;

    // Thêm mới học vấn
    @Operation(summary = "Thêm mới học vấn ", description = "Tạo một mục học vấn mới cho ứng viên đang đăng nhập")
    @PostMapping
    public ResponseEntity<ApiResponse<CandidateEducationResponse>> createEducation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CandidateEducationRequest request) {
        CandidateEducationResponse response = candidateEducationService.createCandidateEducation(userDetails.getUser(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thêm học vấn thành công", response));
    }

    // Lấy danh sách học vấn
    @Operation(summary = "Lấy danh sách học vấn ", description = "Lay tất cả học vấn của ứng viên đăng nhập")
    @GetMapping
    public ResponseEntity<ApiResponse<List<CandidateEducationResponse>>> getEducation(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<CandidateEducationResponse> responses = candidateEducationService.getMyCandidateEducation(userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Lay danh sách học vấn thành công", responses));
    }

    // Lấy chi tiết học vấn
    @Operation(summary = "lấy chi tiết học vấn", description = "Xem chi tiết 1 mục học vấn trong ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CandidateEducationResponse>> getEducationId(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") UUID educationId) {
        CandidateEducationResponse response = candidateEducationService.getEducationById(userDetails.getUser(), educationId);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết học vấn thành công", response));
    }

    // Cập nhật học vấn
    @Operation(summary = "Cập nhật học vấn", description = "Chỉnh sửa thông tin học vấn theo ID")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CandidateEducationResponse>> updateEducation(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("id") UUID educationId,
            @Valid @RequestBody CandidateEducationRequest request) {
        CandidateEducationResponse response = candidateEducationService.updateEducation(userDetails.getUser(), educationId, request);

        return ResponseEntity.ok(ApiResponse.success("Cập nhật học vấn thành công", response));
    }
    
    // Xóa học vấn
    @Operation(summary = "Xóa học vấn", description = "Xóa thông tin học vấn theo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(
            @PathVariable("id") UUID educationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        candidateEducationService.deleteEducation(userDetails.getUser(), educationId);
        return ResponseEntity.ok(ApiResponse.success("Xóa học vấn thành công", null));
    }
}
