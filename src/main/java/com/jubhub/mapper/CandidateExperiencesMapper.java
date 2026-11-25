package com.jubhub.mapper;

import com.jubhub.model.dto.request.CandidateExperiencesRequest;
import com.jubhub.model.dto.response.CandidateExperiencesResponse;
import com.jubhub.model.entity.CandidateExperience;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CandidateExperiencesMapper {
    // Entity -> Response
    CandidateExperiencesResponse toResponse(
            CandidateExperience experience
    );

    // List Entity -> List Response
    List<CandidateExperiencesResponse> toListResponse(
            List<CandidateExperience>experiences
    );

    // Request -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isCurrent", defaultValue = "false")
    CandidateExperience toEntity(
            CandidateExperiencesRequest request
    );

    // update: Request -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isCurrent", defaultValue = "false")
    void updateEntityFromRequest(
            CandidateExperiencesRequest request,
            @MappingTarget CandidateExperience experience
    );
}
