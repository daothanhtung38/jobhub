package com.jubhub.service.interfaces.admin;

import com.jubhub.model.dto.request.SkillCreateRequest;
import com.jubhub.model.dto.request.SkillUpdateRequest;
import com.jubhub.model.dto.response.SkillResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface SkillAdminService {
    // Api admin
    SkillResponse createSkill(SkillCreateRequest request);
    Page<SkillResponse> getAllSkills(int page, int size);
    SkillResponse getSkillById(UUID id);
    SkillResponse updateSkill(UUID id, SkillUpdateRequest request);
    void deleteSkill(UUID id);
}
