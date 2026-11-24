package com.jubhub.mapper;

import com.jubhub.model.dto.request.candidate.CandidateEducationRequest;
import com.jubhub.model.dto.response.CandidateEducationResponse;
import com.jubhub.model.entity.CandidateEducation;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CandidateEducationMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)

    CandidateEducation toEntity(CandidateEducationRequest request);

    CandidateEducationResponse toResponse(CandidateEducation entity);

    List<CandidateEducationResponse> toResponseList(List<CandidateEducation> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)

    void updateEntityFromRequest(CandidateEducationRequest request, @MappingTarget CandidateEducation entity);
}
