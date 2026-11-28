package com.jubhub.mapper;

import com.jubhub.model.dto.request.CandidateSkillCreateRequest;
import com.jubhub.model.dto.request.CandidateSkillUpdateRequest;
import com.jubhub.model.dto.response.CandidateSkillResponse;
import com.jubhub.model.entity.CandidateSkill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CandidateSkillMapper {
    // Từ Entity sang Response
    @Mapping(target = "skillId", source = "skill.id")
    @Mapping(target = "name", source = "skill.name")
    @Mapping(target = "slug", source = "skill.slug")
    @Mapping(target = "category", source = "skill.category")
    CandidateSkillResponse toResponse(CandidateSkill candidateSkill);

    List<CandidateSkillResponse> toResponseList(List<CandidateSkill> candidateSkills);


    // Từ Request -> entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "skill", ignore = true)
    CandidateSkill toEntity(
            CandidateSkillCreateRequest request
    );

    // Update: Từ Request -> Entity hiện tai
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "skill", ignore = true)
    void updateEntityFromRequest(
            CandidateSkillUpdateRequest request,
            @MappingTarget CandidateSkill candidateSkill
    );
}
