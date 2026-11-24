package com.jubhub.controller;

import com.jubhub.common.ApiResponse;
import com.jubhub.model.dto.request.UpdateCandidateProfileRequest;
import com.jubhub.model.dto.response.CandidateProfileResponse;
import com.jubhub.security.CustomUserDetails;
import com.jubhub.service.interfaces.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Candidate Profile", description = "Quản lý thông tin hồ sơ ứng viên")
@RestController
@RequestMapping("/api/candidates/me")
@RequiredArgsConstructor
public class CandidateProfileController {
    private final CandidateService candidateService;

    @Operation(summary = "Xem hồ sơ của tôi", description = "Lấy toàn bộ thông tin hồ sơ của ứng viên đang đăng nhập")
    @GetMapping
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        CandidateProfileResponse response = candidateService.getMyProfile(userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Lấy thông tin thành công", response));
    }

    @Operation(summary = "Cập nhật hồ sơ cá nhân", description = "Cập nhật thông tin chung, kinh nghiệm, mức lương mong muốn")
    @PutMapping
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> updateCandidateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateCandidateProfileRequest request) {
        CandidateProfileResponse response = candidateService.updateMyProfile(userDetails.getUser(), request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin thành công", response));
    }
}
