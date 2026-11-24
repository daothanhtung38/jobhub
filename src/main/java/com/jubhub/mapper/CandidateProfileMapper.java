package com.jubhub.mapper;

import com.jubhub.model.dto.request.UpdateCandidateProfileRequest;
import com.jubhub.model.dto.response.CandidateProfileResponse;
import com.jubhub.model.entity.CandidateProfile;
import com.jubhub.model.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CandidateProfileMapper {

    @Mapping(target = "profileId", source = "profile.id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "fullName", source = "user.fullName")
    @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "avatarUrl", source = "user.avatarUrl")
    @Mapping(target = "createdAt", source = "profile.createdAt")
    @Mapping(target = "updatedAt", source = "profile.updatedAt")
    CandidateProfileResponse toResponse(CandidateProfile profile, User user);

    default CandidateProfileResponse toResponse(CandidateProfile profile) {
        if (profile == null) {
            return null;
        }
        return toResponse(profile, profile.getUser());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "educations", ignore = true)
    @Mapping(target = "experiences", ignore = true)
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "certifications", ignore = true)
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "hiddenCompanies", ignore = true)
    @Mapping(target = "cvs", ignore = true)
    void updateEntityFromRequest(UpdateCandidateProfileRequest request, @MappingTarget CandidateProfile profile);
}
