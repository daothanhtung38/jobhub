package com.jubhub.service.implement;

import com.jubhub.exception.DuplicateResourceException;
import com.jubhub.mapper.CandidateSkillMapper;
import com.jubhub.model.dto.request.CandidateSkillCreateRequest;
import com.jubhub.model.dto.request.CandidateSkillUpdateRequest;
import com.jubhub.model.dto.response.CandidateSkillResponse;
import com.jubhub.model.entity.*;
import com.jubhub.model.enums.EmploymentStatus;
import com.jubhub.model.enums.ProfileVisibility;
import com.jubhub.respository.CandidateProfileRepository;
import com.jubhub.respository.CandidateSkillRepository;
import com.jubhub.respository.SkillRepository;
import com.jubhub.service.interfaces.CandidateSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CandidateSkillServiceImpl implements CandidateSkillService {
    private final CandidateProfileRepository candidateProfileRepository;
    private final CandidateSkillRepository candidateSkillRepository;
    private final CandidateSkillMapper candidateSkillMapper;
    private final SkillRepository skillRepository;

    // Lấy danh sách các skill
    @Override
    @Transactional(readOnly = true)
    public List<CandidateSkillResponse> getSkill(User currentUser) {
        CandidateProfile profile = getMyProfile(currentUser);
        List<CandidateSkill> skills = candidateSkillRepository.findByCandidateId(profile.getId());
        return candidateSkillMapper.toResponseList(skills);
    }

    // Thêm mới skill
    @Override
    @Transactional
    public CandidateSkillResponse createCandidateSkill(User currentUser, CandidateSkillCreateRequest request) {
        CandidateProfile profile = getMyProfile(currentUser);

        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> new DuplicateResourceException(
                        "Không tìm thấy skill với ID " + request.getSkillId(), HttpStatus.NOT_FOUND
                ));

        // tạo candidateSkillId
        CandidateSkillId skillId = CandidateSkillId.builder()
                .candidateId(profile.getId())
                .skillId(skill.getId())
                .build();

        // Kiểm tra xem ứng viên đã thêm kỹ năng này trước đó chưa (tránh trùng)
        if (candidateSkillRepository.existsById(skillId)) {
            throw new DuplicateResourceException("Skill " + skill.getName() + " đã tồn tại trong kĩ năng của bạn", HttpStatus.CONFLICT);
        }

        // Tạo bản ghi mới
        CandidateSkill candidateSkill = CandidateSkill.builder()
                .id(skillId)
                .candidate(profile)
                .skill(skill)
                .proficiencyLevel(request.getProficiencyLevel())
                .yearsOfExperience(request.getYearsOfExperience() != null ? request.getYearsOfExperience() : BigDecimal.ZERO)
                .build();

        CandidateSkill saved = candidateSkillRepository.save(candidateSkill);

        return candidateSkillMapper.toResponse(saved);
    }

    // Cập nhật skill
    @Override
    @Transactional
    public CandidateSkillResponse updateCandidateSkill(User currentUser, UUID skillId, CandidateSkillUpdateRequest request) {
        CandidateProfile profile = getMyProfile(currentUser);

        CandidateSkillId candidateSkillId = CandidateSkillId.builder()
                .candidateId(profile.getId())
                .skillId(skillId)
                .build();

        CandidateSkill candidateSkill = candidateSkillRepository.findById(candidateSkillId)
                .orElseThrow(() -> new DuplicateResourceException("Kỹ năng này chưa có trong cv của bạn", HttpStatus.NOT_FOUND));

        candidateSkill.setProficiencyLevel(request.getProficiencyLevel());
        if (request.getYearsOfExperience() != null) {
            candidateSkill.setYearsOfExperience(request.getYearsOfExperience());
        }

        CandidateSkill update = candidateSkillRepository.save(candidateSkill);

        return candidateSkillMapper.toResponse(update);
    }

    @Override
    @Transactional
    public void deleteCandidateSkill(User currentUser, UUID skillId) {
        CandidateProfile profile = getMyProfile(currentUser);

        CandidateSkillId candidateSkillId = CandidateSkillId.builder()
                .candidateId(profile.getId())
                .skillId(skillId)
                .build();

        if (!candidateSkillRepository.existsById(candidateSkillId)) {
            throw new DuplicateResourceException("Kỹ năng này không tồn tại trong hồ sơ của bạn", HttpStatus.NOT_FOUND);
        }
        candidateSkillRepository.deleteById(candidateSkillId);
    }

    // Hàm phụ: Lấy profile
    private CandidateProfile getMyProfile(User currentUser) {
        return candidateProfileRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> candidateProfileRepository.save(
                        CandidateProfile.builder()
                                .user(currentUser)
                                .yearsOfExperience(BigDecimal.ZERO)
                                .employmentStatus(EmploymentStatus.OPEN_TO_WORK)
                                .isSearchable(false)
                                .profileVisibility(ProfileVisibility.PRIVATE)
                                .build()
                ));
    }
}
