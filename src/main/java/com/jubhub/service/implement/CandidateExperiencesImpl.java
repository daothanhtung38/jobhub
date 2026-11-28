package com.jubhub.service.implement;

import com.jubhub.exception.DuplicateResourceException;
import com.jubhub.mapper.CandidateExperiencesMapper;
import com.jubhub.model.dto.request.CandidateExperiencesRequest;
import com.jubhub.model.dto.response.CandidateExperiencesResponse;
import com.jubhub.model.entity.CandidateExperience;
import com.jubhub.model.entity.CandidateProfile;
import com.jubhub.model.entity.User;
import com.jubhub.model.enums.EmploymentStatus;
import com.jubhub.model.enums.ProfileVisibility;
import com.jubhub.respository.CandidateExperiencesRepository;
import com.jubhub.respository.CandidateProfileRepository;
import com.jubhub.service.interfaces.CandidateExperiencesService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class CandidateExperiencesImpl implements CandidateExperiencesService {
    private final CandidateProfileRepository candidateProfileRepository;
    private final CandidateExperiencesRepository candidateExperiencesRepository;
    private final CandidateExperiencesMapper candidateExperiencesMapper;
    @Override
    public CandidateExperiencesResponse createExperiences(User currentUser, CandidateExperiencesRequest request) {
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

        CandidateExperience experience = candidateExperiencesMapper.toEntity(request);

        experience.setCandidate(profile);

        CandidateExperience savedExperiences = candidateExperiencesRepository.save(experience);

        return candidateExperiencesMapper.toResponse(savedExperiences);
    }

    @Override
    public List<CandidateExperiencesResponse> getCandidateExperiences(User currentUser) {
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
        List<CandidateExperience> experiences = candidateExperiencesRepository.findByCandidateId(profile.getId());

        return candidateExperiencesMapper.toListResponse(experiences);
    }

    @Override
    public CandidateExperiencesResponse getCandidateExperienceById(User currentUser, UUID id) {
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

        CandidateExperience experience =
                candidateExperiencesRepository
                        .findByIdAndCandidateId(id, profile.getId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Không tìm thấy kinh nghiệm làm việc"
                        ));

        return candidateExperiencesMapper.toResponse(experience);
    }

    @Override
    public CandidateExperiencesResponse updateCandidateExperience(User currentUser, UUID id, CandidateExperiencesRequest request) {
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

        // Tìm kinh nhiêm cần cập nhât
        CandidateExperience experience =
                candidateExperiencesRepository
                        .findByIdAndCandidateId(id, profile.getId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Không tìm thấy kinh nghiệm làm việc"
                        ));

        candidateExperiencesMapper.updateEntityFromRequest(
                request,
                experience
        );

        CandidateExperience updatedExperience =
                candidateExperiencesRepository.save(experience);

        return candidateExperiencesMapper.toResponse(updatedExperience);
    }

    @Override
    public void deleteCandidateExperience(User currentUser, UUID id) {
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

        // Tìm kinh nhiêm cần xóa
        CandidateExperience experience =
                candidateExperiencesRepository
                        .findByIdAndCandidateId(id, profile.getId())
                        .orElseThrow(() -> new EntityNotFoundException(
                                "Không tìm thấy kinh nghiệm làm việc"
                        ));

        candidateExperiencesRepository.delete(experience);
    }

    public void validate(CandidateExperiencesRequest request) {
        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        boolean isCurrent = Boolean.TRUE.equals(request.getIsCurrent());

        LocalDate today = LocalDate.now();

        if (startDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu không được để trống");
        }

        if (startDate.isAfter(today)) {
            throw new IllegalArgumentException(
                    "Ngày bắt đầu không được lớn hơn ngày hiện tại"
            );
        }

        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Ngày kết thúc không được trước ngày bắt đầu");
        }

        if (isCurrent && endDate != null) {
            throw new IllegalArgumentException(
                    "Công việc hiện tại không được có ngày kết thúc"
            );
        }

        if (!isCurrent && endDate == null) {
            throw new IllegalArgumentException(
                    "Vui lòng nhập ngày kết thúc công việc"
            );
        }
    }
}
