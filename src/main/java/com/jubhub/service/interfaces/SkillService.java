package com.jubhub.service.interfaces;

import com.jubhub.model.dto.response.SkillResponse;
import org.springframework.data.domain.Page;


public interface SkillService {
    Page<SkillResponse> searchSkills(
            String keyword,
            int page,
            int size
    );
}
