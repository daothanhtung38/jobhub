package com.jubhub.service.interfaces;

import com.jubhub.model.dto.request.CandidateExperiencesRequest;
import com.jubhub.model.dto.response.CandidateExperiencesResponse;
import com.jubhub.model.entity.CandidateExperience;
import com.jubhub.model.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CandidateExperiencesService {
    CandidateExperiencesResponse createExperiences(User currentUser, CandidateExperiencesRequest request);

    List<CandidateExperiencesResponse> getCandidateExperiences(User currentUser);

    CandidateExperiencesResponse getCandidateExperienceById(
            User currentUser,
            UUID id
    );

    CandidateExperiencesResponse updateCandidateExperience(
            User currentUser,
            UUID id,
            CandidateExperiencesRequest request
    );

    void deleteCandidateExperience(
            User currentUser,
            UUID id
    );
}
