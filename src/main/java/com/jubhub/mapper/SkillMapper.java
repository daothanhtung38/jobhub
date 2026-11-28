package com.jubhub.mapper;

import com.jubhub.model.dto.request.CandidateSkillCreateRequest;
import com.jubhub.model.dto.request.SkillCreateRequest;
import com.jubhub.model.dto.request.SkillUpdateRequest;
import com.jubhub.model.dto.response.SkillResponse;
import com.jubhub.model.entity.CandidateSkill;
import com.jubhub.model.entity.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.Locale;

@Mapper(componentModel = "spring")
public interface SkillMapper {
    SkillResponse toResponse(Skill skill);

    List<SkillResponse> toResponseList(List<Skill> skills);

    // Create Request -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "request.name", target = "name",
            qualifiedByName = "normalizeName")
    @Mapping(source = "slug", target = "slug")
    @Mapping(source = "request.category", target = "category",
            qualifiedByName = "normalizeCategory")
    Skill toEntity(SkillCreateRequest request, String slug);

    @Named("normalizeName")
    default String normalizeName(String name) {
        return name != null ? name.trim() : null;
    }

    @Named("normalizeCategory")
    default String normalizeCategory(String category) {
        return category != null
                ? category.trim().toUpperCase(Locale.ROOT)
                : null;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "trimmedName", target = "name")
    @Mapping(source = "slug", target = "slug")
    @Mapping(
            source = "request.category",
            target = "category",
            qualifiedByName = "normalizeCategory"
    )
    void updateEntityFromRequest(
            SkillUpdateRequest request,
            String trimmedName,
            String slug,
            @MappingTarget Skill skill
    );
}
