package com.jubhub.service.interfaces;

import com.jubhub.model.dto.request.UpdateCandidateProfileRequest;
import com.jubhub.model.dto.response.CandidateProfileResponse;
import com.jubhub.model.entity.CandidateProfile;
import com.jubhub.model.entity.User;

import java.util.UUID;

public interface CandidateService {
    CandidateProfileResponse getMyProfile(User currentUser);

    CandidateProfileResponse updateMyProfile(User currentUser, UpdateCandidateProfileRequest request);

    // Xem hồ sơ ứng viên theo ID (Dành cho NTD hoặc Quản trị viên)
    CandidateProfileResponse getProfileByCandidateId(UUID candidateId);
}
