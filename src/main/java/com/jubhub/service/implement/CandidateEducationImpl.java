package com.jubhub.service.implement;

import com.jubhub.exception.DuplicateResourceException;
import com.jubhub.mapper.CandidateEducationMapper;
import com.jubhub.mapper.CandidateProfileMapper;
import com.jubhub.model.dto.request.candidate.CandidateEducationRequest;
import com.jubhub.model.dto.response.CandidateEducationResponse;
import com.jubhub.model.entity.CandidateEducation;
import com.jubhub.model.entity.CandidateProfile;
import com.jubhub.model.entity.User;
import com.jubhub.model.enums.EmploymentStatus;
import com.jubhub.model.enums.ProfileVisibility;
import com.jubhub.respository.CandidateEducationRepository;
import com.jubhub.respository.CandidateProfileRepository;
import com.jubhub.service.interfaces.CandidateEducationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CandidateEducationImpl implements CandidateEducationService {
    private final CandidateEducationRepository candidateEducationRepository;
    private final CandidateEducationMapper candidateEducationMapper;
    private final CandidateProfileRepository candidateProfileRepository;
    @Override
    public CandidateEducationResponse createCandidateEducation(User currentUser, CandidateEducationRequest request) {
        validate(request);
        CandidateProfile profile = candidateProfileRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> {
                    CandidateProfile newProfile = CandidateProfile.builder()
                            .user(currentUser)
                            .yearsOfExperience(BigDecimal.ZERO)
                            .employmentStatus(EmploymentStatus.OPEN_TO_WORK)
                            .isSearchable(false)
                            .profileVisibility(ProfileVisibility.PRIVATE)
                            .build();
                    return candidateProfileRepository.save(newProfile);
                });

        CandidateEducation education = candidateEducationMapper.toEntity(request);

        // gán quyền sở hữu học vấn này cho hồ sơ của mình
        education.setCandidate(profile);
        if (education.getIsCurrent() == null) {
            education.setIsCurrent(false);
        }

        CandidateEducation savedEducation = candidateEducationRepository.save(education);
        return candidateEducationMapper.toResponse(savedEducation);
    }

    @Override
    public List<CandidateEducationResponse> getMyCandidateEducation(User currentUser) {
        CandidateProfile profile = candidateProfileRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> {
                    CandidateProfile newProfile = CandidateProfile.builder()
                            .user(currentUser)
                            .yearsOfExperience(BigDecimal.ZERO)
                            .employmentStatus(EmploymentStatus.OPEN_TO_WORK)
                            .isSearchable(false)
                            .profileVisibility(ProfileVisibility.PRIVATE)
                            .build();
                    return candidateProfileRepository.save(newProfile);
                });
        //Tìm tất cả học vấn của hồ sơ này trong DB (mới nhất lên đầu)
        List<CandidateEducation> education = candidateEducationRepository.findByCandidateIdOrderByStartDateDesc(profile.getId());

        return candidateEducationMapper.toResponseList(education);
    }

    @Override
    public CandidateEducationResponse getEducationById(User currentUser, UUID educationId) {
        CandidateEducation education = findMyEducation(currentUser,educationId);

        return candidateEducationMapper.toResponse(education);
    }

    @Override
    public CandidateEducationResponse updateEducation(User currentUser, UUID educationId, CandidateEducationRequest request) {
        validate(request);

        CandidateEducation education = findMyEducation(currentUser, educationId);

        candidateEducationMapper.updateEntityFromRequest(request, education);

        CandidateEducation updateEducation = candidateEducationRepository.save(education);

        return candidateEducationMapper.toResponse(updateEducation);
    }

    @Override
    public void deleteEducation(User currentUser, UUID educationId) {
        CandidateEducation education = findMyEducation(currentUser, educationId);

        candidateEducationRepository.delete(education);
    }

    // Các hàm phụ
    private CandidateProfile getMyProfile(User currentUser) {
        return candidateProfileRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> {
                    CandidateProfile newProfile = CandidateProfile.builder()
                            .user(currentUser)
                            .yearsOfExperience(BigDecimal.ZERO)
                            .employmentStatus(EmploymentStatus.OPEN_TO_WORK)
                            .isSearchable(false)
                            .profileVisibility(ProfileVisibility.PRIVATE)
                            .build();
                    return candidateProfileRepository.save(newProfile);
                });
    }

    public void validate(CandidateEducationRequest request) {
        boolean isCurrent = Boolean.TRUE.equals(request.getIsCurrent());

        if (!isCurrent && request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("Ngày kết thúc khóa học không được xảy ra trước ngày bắt đầu");
        }
    }

    public CandidateEducation findMyEducation(User currentUser, UUID educationId) {
        CandidateProfile profile = getMyProfile(currentUser);

        return candidateEducationRepository.findByIdAndCandidateId(educationId, profile.getId())
                .orElseThrow(() -> new DuplicateResourceException(
                        "Không tìm thấy học vấn với ID: " + educationId + "hoặc bạn không có quyền truy cập",
                        HttpStatus.NOT_FOUND
                ));
    }
}
