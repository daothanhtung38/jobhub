package com.jubhub.service.interfaces;

import com.jubhub.model.dto.request.candidate.CandidateEducationRequest;
import com.jubhub.model.dto.response.CandidateEducationResponse;
import com.jubhub.model.entity.User;

import java.util.List;
import java.util.UUID;

public interface CandidateEducationService {
    CandidateEducationResponse createCandidateEducation(User currentUser, CandidateEducationRequest request);

    List<CandidateEducationResponse> getMyCandidateEducation(User currentUser);

    CandidateEducationResponse getEducationById(User currentUser, UUID educationId);

    CandidateEducationResponse updateEducation(User currentUser, UUID educationId, CandidateEducationRequest request);

    void deleteEducation(User currentUser, UUID educationid);
}
