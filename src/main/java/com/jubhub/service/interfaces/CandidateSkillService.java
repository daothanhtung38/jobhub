package com.jubhub.service.interfaces;

import com.jubhub.model.dto.request.CandidateSkillCreateRequest;
import com.jubhub.model.dto.request.CandidateSkillUpdateRequest;
import com.jubhub.model.dto.response.CandidateSkillResponse;
import com.jubhub.model.entity.User;

import java.util.List;
import java.util.UUID;

public interface CandidateSkillService {
    List<CandidateSkillResponse> getSkill(User currentUser);

    CandidateSkillResponse createCandidateSkill(User currentUser, CandidateSkillCreateRequest request);

    CandidateSkillResponse updateCandidateSkill(User currentUser, UUID skillId, CandidateSkillUpdateRequest request);

    void deleteCandidateSkill(User currentUser, UUID skillId);
}
